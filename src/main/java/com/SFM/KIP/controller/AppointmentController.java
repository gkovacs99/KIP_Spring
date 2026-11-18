package com.SFM.KIP.controller;

import com.SFM.KIP.dto.AppointmentSlot;
import com.SFM.KIP.dto.DoctorOption;
import com.SFM.KIP.entity.User;
import com.SFM.KIP.service.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/my-appointments")
    public String myAppointments(Model model, Authentication auth) {

        model.addAttribute(
                "appointments",
                appointmentService.getMyAppointments(auth.getName())
        );

        model.addAttribute(
                "departments",
                appointmentService.getDepartments()
        );

        return "my-appointments";
    }

    @GetMapping("/appointments/doctors")
    @ResponseBody
    public List<DoctorOption> getDoctors(
            @RequestParam Long departmentId
    ) {

        return appointmentService.getDoctors(departmentId)
                .stream()
                .map(doctor -> new DoctorOption(
                        doctor.getId(),
                        doctor.getName()
                ))
                .toList();
    }

    @GetMapping("/appointments/slots")
    @ResponseBody
    public List<AppointmentSlot> getAvailableSlots(
            @RequestParam Long doctorId
    ) {

        return appointmentService.getAvailableSlots(doctorId);
    }

    @PostMapping("/my-appointments/book")
    public String book(
            @RequestParam Long doctorId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            Authentication auth,
            RedirectAttributes ra
    ) {

        try {
            appointmentService.bookAppointment(
                    auth.getName(),
                    doctorId,
                    startTime
            );

            ra.addFlashAttribute(
                    "message",
                    "Sikeresen lefoglalta az időpontot."
            );

        } catch (IllegalArgumentException e) {

            ra.addFlashAttribute("error", e.getMessage());

        }

        return "redirect:/my-appointments";
    }

    @PostMapping("/my-appointments/{id}/cancel")
    public String cancel(
            @PathVariable Long id,
            Authentication auth,
            RedirectAttributes ra
    ) {

        try {
            appointmentService.cancelAppointment(auth.getName(), id);

            ra.addFlashAttribute(
                    "message",
                    "Az időpont lemondása sikeres."
            );

        } catch (IllegalArgumentException e) {

            ra.addFlashAttribute("error", e.getMessage());

        }

        return "redirect:/my-appointments";
    }
}