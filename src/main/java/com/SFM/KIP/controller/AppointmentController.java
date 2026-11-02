package com.SFM.KIP.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @GetMapping("/my-appointments")
    public String myAppointments(Model model){
        model.addAttribute("appointments", List.of());

        return "my-appointments";
    }

    //foglalás
    @PostMapping
    public String book(@RequestParam Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startTime, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Sikeres foglalás.");

        return "redirect:/dashboard";
    }

    //saját időpont törtlése
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Időpont törlése");

        return "redirect:/dashboard";
    }

    //nurse lemondás+újrafoglalás űrlap
    @GetMapping ("/{id}/reschedule")
    public String rescheduleForm(@PathVariable Long id, Model model){
        model.addAttribute("appointmentId", id);
        model.addAttribute("freeSlots", List.of());

        return "reschedule";
    }

    //nurse lemondás + újrafoglalás mentés
    @PostMapping("/{id}/reschedule")
    public String reschedule(@PathVariable Long id,  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Iőpont lemondva, új időponmmt felajánlva.");

        return "redirect:/dashboard";
    }

}
