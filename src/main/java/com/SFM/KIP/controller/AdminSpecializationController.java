
package com.SFM.KIP.controller;

import com.SFM.KIP.entity.Specialization;
import com.SFM.KIP.repository.SpecializationRepository;
import com.SFM.KIP.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/specializations")
public class AdminSpecializationController {

    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;

    public AdminSpecializationController(
            SpecializationRepository specializationRepository,
            UserRepository userRepository
    ) {
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
    }

    // SPECIALIZÁCIÓK LISTÁZÁSA

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "specializations",
                specializationRepository.findAll()
        );

        return "admin/specializations";
    }

    // ÚJ SPECIALIZÁCIÓ ŰRLAPJA

    @GetMapping("/new")
    public String newSpecialization(Model model) {

        model.addAttribute("specialization", new Specialization());
        model.addAttribute("pageTitle", "Új specializáció");

        return "admin/specialization-form";
    }

    // SPECIALIZÁCIÓ SZERKESZTÉSE

    @GetMapping("/{id}/edit")
    public String editSpecialization(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ) {

        Specialization specialization =
                specializationRepository.findById(id).orElse(null);

        if (specialization == null) {
            ra.addFlashAttribute(
                    "error",
                    "A specializáció nem található."
            );

            return "redirect:/admin/specializations";
        }

        model.addAttribute("specialization", specialization);
        model.addAttribute("pageTitle", "Specializáció szerkesztése");

        return "admin/specialization-form";
    }

    // SPECIALIZÁCIÓ MENTÉSE

    @PostMapping
    public String saveSpecialization(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Model model,
            RedirectAttributes ra
    ) {

        boolean isNew = id == null;

        String trimmedName = name == null ? "" : name.trim();
        String trimmedDescription =
                description == null ? "" : description.trim();

        Specialization specialization;

        if (isNew) {
            specialization = new Specialization();
        } else {
            specialization =
                    specializationRepository.findById(id).orElse(null);

            if (specialization == null) {
                ra.addFlashAttribute(
                        "error",
                        "A módosítani kívánt specializáció nem található."
                );

                return "redirect:/admin/specializations";
            }
        }

        // Kötelező mezők ellenőrzése

        if (trimmedName.isBlank()) {
            model.addAttribute(
                    "error",
                    "A specializáció neve kötelező."
            );

            model.addAttribute("specialization", specialization);
            specialization.setName(trimmedName);
            specialization.setDescription(trimmedDescription);
            model.addAttribute(
                    "pageTitle",
                    isNew ? "Új specializáció" : "Specializáció szerkesztése"
            );

            return "admin/specialization-form";
        }

        // Név egyediségének ellenőrzése

        boolean nameExists;

        if (isNew) {
            nameExists =
                    specializationRepository.existsByNameIgnoreCase(trimmedName);
        } else {
            nameExists =
                    specializationRepository.existsByNameIgnoreCaseAndIdNot(
                            trimmedName,
                            id
                    );
        }

        if (nameExists) {
            specialization.setName(trimmedName);
            specialization.setDescription(trimmedDescription);

            model.addAttribute(
                    "error",
                    "Ilyen nevű specializáció már létezik."
            );

            model.addAttribute("specialization", specialization);
            model.addAttribute(
                    "pageTitle",
                    isNew ? "Új specializáció" : "Specializáció szerkesztése"
            );

            return "admin/specialization-form";
        }

        // Adatok mentése

        specialization.setName(trimmedName);
        specialization.setDescription(trimmedDescription);

        specializationRepository.save(specialization);

        ra.addFlashAttribute(
                "message",
                isNew
                        ? "A specializáció sikeresen létrehozva."
                        : "A specializáció sikeresen módosítva."
        );

        return "redirect:/admin/specializations";
    }

    // SPECIALIZÁCIÓ TÖRLÉSE

    @PostMapping("/{id}/delete")
    public String deleteSpecialization(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {

        Specialization specialization =
                specializationRepository.findById(id).orElse(null);

        if (specialization == null) {
            ra.addFlashAttribute(
                    "error",
                    "A specializáció nem található."
            );

            return "redirect:/admin/specializations";
        }

        // Ellenőrizzük, hogy van-e hozzá rendelt orvos

        if (userRepository.existsBySpecialization_Id(id)) {
            ra.addFlashAttribute(
                    "error",
                    "A specializáció nem törölhető, mert legalább egy orvoshoz hozzá van rendelve."
            );

            return "redirect:/admin/specializations";
        }

        specializationRepository.delete(specialization);

        ra.addFlashAttribute(
                "message",
                "A specializáció sikeresen törölve."
        );

        return "redirect:/admin/specializations";
    }
}
