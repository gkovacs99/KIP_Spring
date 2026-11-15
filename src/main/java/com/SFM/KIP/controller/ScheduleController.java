package com.SFM.KIP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ScheduleController {

    @GetMapping("/schedule")
    public String scedule(Model model){
        model.addAttribute("doctors", List.of());
        model.addAttribute("schedule", List.of());

        return "schedule";
    }

    @PostMapping("/schedule")
    public String saveSchedule(RedirectAttributes ra){
        ra.addFlashAttribute("message", "Időbeosztás mentve");
        return "redirect:/schedule";
    }

    @GetMapping("/rooms/assignments")
    public String roomAssignments(Model model){
        model.addAttribute("rooms", List.of());
        model.addAttribute("assignments", List.of());

        return "room-assignments";
    }

    @PostMapping("/rooms/assignments")
    public String assignRoom(@RequestParam Long roomId, @RequestParam Long consultationDayId, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Rendelő kiosztva");

        return "redirect:/rooms/assignments";
    }
}
