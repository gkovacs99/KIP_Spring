package com.SFM.KIP.controller;

import com.SFM.KIP.repository.DepartmentRepository;
import com.SFM.KIP.repository.RoomRepository;
import com.SFM.KIP.repository.SpecializationRepository;
import com.SFM.KIP.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpecializationRepository specializationRepository;

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("departmentCount", departmentRepository.count());
        model.addAttribute("roomCount", roomRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("specializationCount", specializationRepository.count());

        return "admin/dashboard";
    }
}