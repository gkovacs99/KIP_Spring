package com.SFM.KIP.controller;

import com.SFM.KIP.dto.RegisterForm;
import com.SFM.KIP.service.UserService;
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

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

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
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute RegisterForm registerForm, BindingResult result, RedirectAttributes ra){

        if (!result.hasFieldErrors("email") && userService.emailExists(registerForm.email().trim().toLowerCase())){
            result.rejectValue("email", "taken", "A megadott e-mail cím már regisztrálva van.");
        }

        if(result.hasErrors()){
            return "register";
        }

        userService.registerPatient(registerForm);
        ra.addFlashAttribute("message", "Sikeres regisztráció, most már beléphet.");
        return "redirec:/login";
    }

}
