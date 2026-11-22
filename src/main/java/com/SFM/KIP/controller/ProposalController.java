package com.SFM.KIP.controller;

import com.SFM.KIP.entity.Appointment;
import com.SFM.KIP.entity.AppointmentStatus;
import com.SFM.KIP.repository.AppointmentRepository;
import com.SFM.KIP.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProposalController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/proposals")
    public String myProposals(Model model){
        model.addAttribute("proposals", List.of());

        return "proposals";
    }

    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id, RedirectAttributes ra){

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.BOOKED);
        appointmentRepository.save(appointment);

        ra.addFlashAttribute("message", "Az új időpontot lefoglaltad.");

        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable Long id, RedirectAttributes ra){

        Appointment appointment = appointmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        ra.addFlashAttribute("message", "Az ajánlott időpomntot elutasítottad");

        return "redirect:/dashboard";
    }
}
