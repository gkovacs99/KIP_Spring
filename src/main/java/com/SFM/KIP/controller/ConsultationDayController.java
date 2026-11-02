package com.SFM.KIP.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/consultation-days")
public class ConsultationDayController {

    @GetMapping
    public String list(Model model){
        model.addAttribute("consultationDays", List.of());

        return "consultation-days";
    }

    @GetMapping("/new")
    public String NewForm(){
        return "consultation-day-form";
    }

    @PostMapping
    public String create(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime from,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime to,
                         RedirectAttributes ra) {
        ra.addFlashAttribute("message", "Rendelési nap rögzítve.");
        return "redirect:/consultation-days";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Rendelési nap törölve.");

        return "redirect:/consultation-days";
    }


}
