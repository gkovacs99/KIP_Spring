package com.SFM.KIP.controller;

import com.SFM.KIP.entity.DutyShift;
import com.SFM.KIP.entity.DutyStatus;
import com.SFM.KIP.entity.User;
import com.SFM.KIP.repository.DutyShiftRepository;
import com.SFM.KIP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("duties")
public class DutyController {

    @Autowired
    private DutyShiftRepository dutyShiftRepository;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isSupervisor(User user) {
        String role = user.getRole().getName();
        return role.equals("SENIOR") || role.equals("DIRECTOR");
    }

    private boolean hasRole(User user, String role) {
        return user.getRole().getName().equals(role);
    }

    @GetMapping
    public String myDuties(Model model, Authentication auth) {

        User currentUser = getCurrentUser(auth);
        String role = currentUser.getRole().getName();

        model.addAttribute("currentRole", role);

        // Minden nem nővér saját ügyeletei
        if (!hasRole(currentUser, "NURSE")) {

            model.addAttribute(
                    "myDuties",
                    dutyShiftRepository.findAllByEmployee_IdOrderByDateAscStartTimeAsc(
                            currentUser.getId()
                    )
            );

            model.addAttribute(
                    "nurses",
                    userRepository.findAllByRole_Name("NURSE")
            );
        }

        // Jóváhagyásra váró kérelmek
        if (isSupervisor(currentUser)) {

            model.addAttribute(
                    "pendingDuties",
                    dutyShiftRepository.findAllByStatusOrderByDateAscStartTimeAsc(
                            DutyStatus.PENDING
                    )
            );

            List<User> doctors = userRepository.findAll().stream()
                            .filter(u -> List.of("DOCTOR", "SENIOR", "DIRECTOR")
                                    .contains(u.getRole().getName())).toList();

            model.addAttribute("doctors", doctors);


            model.addAttribute(
                    "nurses",
                    userRepository.findAllByRole_Name("NURSE")
            );
        }

        return "duties";
    }

    @PostMapping
    public String createDuty(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam Long nurseId,
            Authentication auth,
            RedirectAttributes ra) {

        User doctor = getCurrentUser(auth);

        if (hasRole(doctor, "NURSE")) {
            ra.addFlashAttribute("error",
                    "Nővér nem vehet fel önkéntes ügyeletet.");

            return "redirect:/duties";
        }

        if (!endTime.isAfter(startTime)) {
            ra.addFlashAttribute("error",
                    "A befejezési időpontnak későbbinek kell lennie a kezdésnél.");

            return "redirect:/duties";
        }

        User nurse = userRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (!hasRole(nurse, "NURSE")) {
            ra.addFlashAttribute("error",
                    "A kiválasztott felhasználó nem nővér.");

            return "redirect:/duties";
        }

        DutyShift duty = new DutyShift();

        duty.setEmployee(doctor);
        duty.setNurse(nurse);
        duty.setDate(date);
        duty.setStartTime(startTime);
        duty.setEndTime(endTime);
        duty.setMandatory(false);
        duty.setStatus(DutyStatus.PENDING);
        duty.setCreatedBy(doctor);

        dutyShiftRepository.save(duty);

        ra.addFlashAttribute("message",
                "Az ügyeleti kérelmet sikeresen elküldted.");

        return "redirect:/duties";
    }

    // Orvos saját, függőben lévő kérelmének törlése
    @PostMapping("/{id}/delete")
    public String deleteDuty(
            @PathVariable Long id,
            Authentication auth,
            RedirectAttributes ra) {

        User currentUser = getCurrentUser(auth);

        DutyShift duty = dutyShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Duty not found"));

        if (!hasRole(currentUser, "DOCTOR")
                || !duty.getEmployee().getId().equals(currentUser.getId())
                || duty.getStatus() != DutyStatus.PENDING
                || duty.isMandatory()) {

            ra.addFlashAttribute("error", "Ezt az ügyeletet nem törölheted.");
            return "redirect:/duties";
        }

        dutyShiftRepository.delete(duty);

        ra.addFlashAttribute("message", "Az ügyeleti kérelmet törölted.");

        return "redirect:/duties";
    }

    // Felettes elfogadja az ügyeletet
    @PostMapping("/{id}/accept")
    public String accept(
            @PathVariable Long id,
            Authentication auth,
            RedirectAttributes ra) {

        User currentUser = getCurrentUser(auth);

        if (!isSupervisor(currentUser)) {
            return "redirect:/duties";
        }

        DutyShift duty = dutyShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Duty not found"));

        if (duty.getStatus() != DutyStatus.PENDING) {
            ra.addFlashAttribute("error", "Ez az ügyelet már nem vár jóváhagyásra.");
            return "redirect:/duties";
        }

        duty.setStatus(DutyStatus.APPROVED);
        dutyShiftRepository.save(duty);

        ra.addFlashAttribute("message", "Az ügyeletet elfogadtad.");

        return "redirect:/duties";
    }

    // Felettes elutasítja az ügyeletet
    @PostMapping("/{id}/decline")
    public String decline(
            @PathVariable Long id,
            Authentication auth,
            RedirectAttributes ra) {

        User currentUser = getCurrentUser(auth);

        if (!isSupervisor(currentUser)) {
            return "redirect:/duties";
        }

        DutyShift duty = dutyShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Duty not found"));

        if (duty.getStatus() != DutyStatus.PENDING) {
            ra.addFlashAttribute("error", "Ez az ügyelet már nem vár elbírálásra.");
            return "redirect:/duties";
        }

        duty.setStatus(DutyStatus.REJECTED);
        dutyShiftRepository.save(duty);

        ra.addFlashAttribute("message", "Az ügyeletet elutasítottad.");

        return "redirect:/duties";
    }

    // Felettes kötelező ügyeletet hoz létre
    @PostMapping("/mandatory")
    public String makeMandatory(
            @RequestParam Long doctorId,
            @RequestParam Long nurseId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            Authentication auth,
            RedirectAttributes ra) {

        User supervisor = getCurrentUser(auth);

        if (!isSupervisor(supervisor)) {
            return "redirect:/duties";
        }

        if (!endTime.isAfter(startTime)) {
            ra.addFlashAttribute("error", "A befejezési időpontnak későbbinek kell lennie a kezdésnél.");
            return "redirect:/duties";
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        User nurse = userRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (!hasRole(nurse, "NURSE")) {
            ra.addFlashAttribute("error", "Érvénytelen nővér.");
            return "redirect:/duties";
        }

        DutyShift duty = new DutyShift();
        duty.setEmployee(doctor);
        duty.setNurse(nurse);
        duty.setDate(date);
        duty.setStartTime(startTime);
        duty.setEndTime(endTime);
        duty.setMandatory(true);
        duty.setStatus(DutyStatus.APPROVED);
        duty.setCreatedBy(supervisor);

        dutyShiftRepository.save(duty);

        ra.addFlashAttribute("message", "A kötelező ügyeletet sikeresen létrehoztad.");

        return "redirect:/duties";
    }
}
