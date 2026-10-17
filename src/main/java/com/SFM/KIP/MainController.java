package com.SFM.KIP;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "index.html";
    }

    @RequestMapping("/login")
    public String login() {
        return "login.html";
    }

    @RequestMapping("/register")
    public String register() {
        return "register.html";
    }

    @RequestMapping("/dashboard")
    public String dashboard() {
        return "dashboard.html";
    }

    @RequestMapping("/dashboard/reservation")
    public String dashboardReservation() {
        return "dashboardReservation.html";
    }

    @RequestMapping("/dashboard/doctors")
    public String dashboardDoctors() {
        return "dashboardDoctors.html";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "Hello world!";
    }
}
