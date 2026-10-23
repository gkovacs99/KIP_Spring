package com.SFM.KIP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DoctorController {

    @GetMapping("/doctors")
    public String searchDoctors(){
        return "doctors";
    }

    @GetMapping("/doctors/{id}")
    public String doctorDetails(){
        return "doctor-details";
    }

    @GetMapping("/doctors/{id}/appointments")
    public String doctorAppointments(){
        return "doctor-appointments";
    }
}
