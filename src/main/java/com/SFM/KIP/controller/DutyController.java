package com.SFM.KIP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/duties")
public class DutyController {

    @GetMapping("/my")
    public String myDuties(Model model){
        model.addAttribute("duties", List.of());

        return "my-duties";
    }

    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Ügyelet elfogadva");

        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Ügyelet elutasítva.");

        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/mandatory")
    public String makeMandatory(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Az ügyelet kötelező lett..");

        return "redirect:/schedule";
    }
}
