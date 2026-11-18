package com.SFM.KIP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", List.of());

        return "admin/employees";
    }

    @GetMapping("/employees/new")
    public String newEmployee() {
        return "admin/employee-form";
    }

    @PostMapping("/employees")
    public String createEmployee(RedirectAttributes ra){
        ra.addFlashAttribute("message", "Alkalmazott felvéve");

        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam String role, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Szerepkör módosítva");

        return "redirect:/admin/employees";
    }
}