package com.SFM.KIP.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth, HttpServletRequest request){
        String username = auth.getName;

        model.addAttribute("upcomingAppointments", List.of());
        model.addAttribute("pendingProposals", List.of());

        if(request.isUserInRole("NURSE")){
            model.addAttribute("pendingDuties", List.of());
            model.addAttribute("doctorAppointments", List.of());
        }
        if(request.isUserInRole("DOCTOR")){
            model.addAttribute("myConsultationDays", List.of());
        }
        if(request.isUserInRole("Senior")){
            model.addAttribute("unassignedRooms", 0);
        }
        if(request.isUserInRole("DIRECTOR")){
            model.addAttribute("employeeCount", 0);
        }
        return "dashboard";
    }
}
