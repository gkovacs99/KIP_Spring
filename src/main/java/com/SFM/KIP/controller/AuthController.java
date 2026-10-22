package com.SFM.KIP.controller;

import com.SFM.KIP.dto.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.Authenticator;

@Controller
public class AuthController {

    @GetMapping("/")
    public String index (Authentication auth) {
        if (auth != null && auth.isAuthenticated()){
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("registerForm", new RegisterForm("", "", ""));
        return "regisret";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute RegisterForm registerForm, BindingResult result, RedirectAttributes ra){
        if(result.hasErrors()){
            return "register";
        }
        //felhasználót majd menteni kell USER szerepkörrel
        ra.addFlashAttribute("message", "Sikeres regisztráció, most már beléphet.");
        return "redirec:/login";
    }

}
