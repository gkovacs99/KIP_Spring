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
@RequestMapping ("/proposals")
public class ProposalController {

    @GetMapping
    public String myProposals(Model model){
        model.addAttribute("proposals", List.of());

        return "proposals";
    }

    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Az új időpontot lefoglaltad.");

        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable Long id, RedirectAttributes ra){
        ra.addFlashAttribute("message", "Az ajánlott időpomntot elutasítottad");

        return "redirect:/dashboard";
    }
}
