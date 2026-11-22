package com.SFM.KIP.controller;

import com.SFM.KIP.entity.DutyStatus;
import com.SFM.KIP.entity.User;
import com.SFM.KIP.repository.DutyShiftRepository;
import com.SFM.KIP.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DutyShiftRepository dutyShiftRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth, HttpServletRequest request){
        String username = auth.getName();
        User currentUser = getCurrentUser(auth);

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("currentRole", user.getRole().getName());

        model.addAttribute("upcomingAppointments", List.of());
        model.addAttribute("pendingProposals", List.of());
        model.addAttribute("pendingDuties", List.of());
        model.addAttribute("doctorAppointments", List.of());
        model.addAttribute("myConsultationDays", List.of());
        model.addAttribute("unassignedRooms", 0);
        model.addAttribute("employeeCount", 0);

        model.addAttribute("currentRole", currentUser.getRole().getName());

        if (!currentUser.getRole().getName().equals("NURSE")) {
            model.addAttribute("myDuties",
                    dutyShiftRepository.findAllByEmployee_IdOrderByDateAscStartTimeAsc(
                            currentUser.getId()
                    ));
        }

        if (currentUser.getRole().getName().equals("NURSE")) {
            model.addAttribute("pendingDuties",
                    dutyShiftRepository.findAllByNurse_IdOrderByDateAscStartTimeAsc(
                            currentUser.getId()
                    ));
        }

        if (currentUser.getRole().getName().equals("SENIOR")
                || currentUser.getRole().getName().equals("DIRECTOR")) {

            model.addAttribute("pendingDuties",
                    dutyShiftRepository.findAllByStatusOrderByDateAscStartTimeAsc(
                            DutyStatus.PENDING
                    ));
        }

        return "dashboard";
    }
}
