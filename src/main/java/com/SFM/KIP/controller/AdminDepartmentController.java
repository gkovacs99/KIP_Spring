
package com.SFM.KIP.controller;

import com.SFM.KIP.entity.Department;
import com.SFM.KIP.repository.DepartmentRepository;
import com.SFM.KIP.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/departments")
public class AdminDepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments",
                departmentRepository.findAll());

        return "admin/departments";
    }

    @GetMapping("/new")
    public String newDepartment(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("pageTitle", "Új osztály");

        return "admin/department-form";
    }

    @GetMapping("/{id}/edit")
    public String editDepartment(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ) {
        Department department = departmentRepository.findById(id)
                .orElse(null);

        if (department == null) {
            ra.addFlashAttribute("error", "Az osztály nem található.");
            return "redirect:/admin/departments";
        }

        model.addAttribute("department", department);
        model.addAttribute("pageTitle", "Osztály szerkesztése");

        return "admin/department-form";
    }

    @PostMapping
    public String saveDepartment(
            @ModelAttribute Department department,
            RedirectAttributes ra
    ) {
        departmentRepository.save(department);

        ra.addFlashAttribute("message", "Az osztály sikeresen mentve.");

        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {
        if (!departmentRepository.existsById(id)) {
            ra.addFlashAttribute("error", "Az osztály nem található.");
            return "redirect:/admin/departments";
        }

        if (roomRepository.existsByDepartment_Id(id)) {
            ra.addFlashAttribute("error", "Az osztály nem törölhető, mert még tartoznak hozzá szobák."
            );

            return "redirect:/admin/departments";
        }

        departmentRepository.deleteById(id);

        ra.addFlashAttribute("message", "Az osztály sikeresen törölve.");

        return "redirect:/admin/departments";
    }
}
