package com.SFM.KIP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class DoctorController {

    @GetMapping("/doctors")
        public String searchDoctors(@RequestParam(required = false) String q,
                @RequestParam(required = false) String specialty,
                Model model){
        model.addAttribute("q", q);
        model.addAttribute("speciality", specialty);
        model.addAttribute("doctors", List.of());

        return "doctors";
        }


    @GetMapping("/doctors/{id}")
    public String doctorDetails(@PathVariable Long id, Model model){
        model.addAttribute("doctorId", id);
        model.addAttribute("freeSlots", List.of());

        return "doctor-details";
    }

    @GetMapping("/doctors/{id}/appointments")
    public String doctorAppointments(@PathVariable Long id, Model model){
        model.addAttribute("doctorId", id);
        model.addAttribute("appointments", List.of());

        return "doctor-appointments";
    }
}
