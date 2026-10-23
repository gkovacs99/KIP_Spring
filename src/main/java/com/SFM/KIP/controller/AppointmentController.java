package com.SFM.KIP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppointmentController {

    @GetMapping("/my-appointments")
    public String myAppointments(){
        return "my-appointments";
    }

    //foglalás
    @PostMapping
    public String book(){
        return "redirect:/dashboard";
    }

    //saját időpont törtlése
    @PostMapping("/{id}/cancel")
    public String cancel(){
        return "redirect:/dashboard";
    }

    //nurse lemondás+újrafoglalás űrlap
    @GetMapping ("/{id}/reschedule")
    public String rescheduleForm(){
     return "reschedule";
    }

    //nurse lemondás + újrafoglalás mentés
    @PostMapping("/{id}/reschedule")
    public String reschedule(){
        return "redirect:/dashboard";
    }

}
