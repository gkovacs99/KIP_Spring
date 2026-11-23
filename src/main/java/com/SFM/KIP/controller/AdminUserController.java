
package com.SFM.KIP.controller;

import com.SFM.KIP.dto.UserForm;
import com.SFM.KIP.entity.*;
import com.SFM.KIP.repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            SpecializationRepository specializationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.specializationRepository = specializationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // FELHASZNÁLÓK LISTÁZÁSA

    @GetMapping
    public String list(Model model) {
        model.addAttribute(
                "users",
                userRepository.findAllByOrderByNameAsc()
        );

        return "admin/users";
    }

    // ÚJ FELHASZNÁLÓ ŰRLAPJA

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("pageTitle", "Új felhasználó");

        addFormAttributes(model);

        return "admin/user-form";
    }

    // FELHASZNÁLÓ SZERKESZTÉSE

    @GetMapping("/{id}/edit")
    public String editUser(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("error", "A felhasználó nem található.");
            return "redirect:/admin/users";
        }

        UserForm form = new UserForm();

        form.setId(user.getId());
        form.setName(user.getName());
        form.setBirthDate(user.getBirthDate());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setActive(user.isActive());

        form.setRoleId(user.getRole().getId());

        if (user.getDepartment() != null) {
            form.setDepartmentId(user.getDepartment().getId());
        }

        if (user.getSpecialization() != null) {
            form.setSpecializationId(user.getSpecialization().getId());
        }

        if (user.getAssignedDoctor() != null) {
            form.setAssignedDoctorId(user.getAssignedDoctor().getId());
        }

        model.addAttribute("userForm", form);
        model.addAttribute("pageTitle", "Felhasználó szerkesztése");

        addFormAttributes(model);

        return "admin/user-form";
    }

    // FELHASZNÁLÓ LÉTREHOZÁSA / MÓDOSÍTÁSA

    @PostMapping
    public String saveUser(
            @ModelAttribute("userForm") UserForm form,
            Model model,
            RedirectAttributes ra
    ) {
        boolean isNew = form.getId() == null;

        User user;

        if (isNew) {
            user = new User();
        } else {
            user = userRepository.findById(form.getId()).orElse(null);

            if (user == null) {
                ra.addFlashAttribute("error", "A felhasználó nem található.");
                return "redirect:/admin/users";
            }
        }

        // Kötelező mezők ellenőrzése

        if (form.getName() == null || form.getName().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()
                || form.getPhone() == null || form.getPhone().isBlank()
                || form.getRoleId() == null) {

            model.addAttribute("error", "Minden kötelező mezőt ki kell tölteni.");
            model.addAttribute("pageTitle",
                    isNew ? "Új felhasználó" : "Felhasználó szerkesztése");

            addFormAttributes(model);
            return "admin/user-form";
        }

        // Új felhasználónál kötelező a jelszó

        if (isNew && (form.getPassword() == null || form.getPassword().isBlank())) {
            model.addAttribute("error", "Új felhasználónál kötelező jelszót megadni.");
            model.addAttribute("pageTitle", "Új felhasználó");

            addFormAttributes(model);
            return "admin/user-form";
        }

        // E-mail egyediségének ellenőrzése

        var existingUser = userRepository.findByEmail(form.getEmail().trim());

        if (existingUser.isPresent()
                && !existingUser.get().getId().equals(form.getId())) {

            model.addAttribute("error", "Ez az e-mail-cím már használatban van.");
            model.addAttribute("pageTitle",
                    isNew ? "Új felhasználó" : "Felhasználó szerkesztése");

            addFormAttributes(model);
            return "admin/user-form";
        }

        // Szerepkör betöltése

        Role role = roleRepository.findById(form.getRoleId()).orElse(null);

        if (role == null) {
            model.addAttribute("error", "A kiválasztott szerepkör nem található.");
            model.addAttribute("pageTitle",
                    isNew ? "Új felhasználó" : "Felhasználó szerkesztése");

            addFormAttributes(model);
            return "admin/user-form";
        }

        // Alapadatok

        user.setName(form.getName().trim());
        user.setBirthDate(form.getBirthDate());
        user.setEmail(form.getEmail().trim());
        user.setPhone(form.getPhone().trim());
        user.setActive(form.isActive());
        user.setRole(role);

        // Jelszó: csak akkor módosítjuk, ha megadtak újat

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        // Osztály

        Department department = null;

        if (form.getDepartmentId() != null) {
            department = departmentRepository
                    .findById(form.getDepartmentId())
                    .orElse(null);

            if (department == null) {
                model.addAttribute("error", "A kiválasztott osztály nem található.");
                model.addAttribute("pageTitle",
                        isNew ? "Új felhasználó" : "Felhasználó szerkesztése");

                addFormAttributes(model);
                return "admin/user-form";
            }
        }

        user.setDepartment(department);

        // Szakterület: csak DOCTOR szerepkörnél

        user.setSpecialization(null);

        if ("DOCTOR".equals(role.getName())
                && form.getSpecializationId() != null) {

            Specialization specialization = specializationRepository
                    .findById(form.getSpecializationId())
                    .orElse(null);

            if (specialization == null) {
                model.addAttribute("error", "A kiválasztott szakterület nem található.");
                model.addAttribute("pageTitle",
                        isNew ? "Új felhasználó" : "Felhasználó szerkesztése");

                addFormAttributes(model);
                return "admin/user-form";
            }

            user.setSpecialization(specialization);
        }

        // Hozzárendelt orvos: csak NURSE szerepkörnél

        user.setAssignedDoctor(null);

        if ("NURSE".equals(role.getName())
                && form.getAssignedDoctorId() != null) {

            User doctor = userRepository
                    .findById(form.getAssignedDoctorId())
                    .orElse(null);

            if (doctor == null
                    || !"DOCTOR".equals(doctor.getRole().getName())
                    || !doctor.isActive()) {

                model.addAttribute("error",
                        "A kiválasztott orvos nem érvényes vagy inaktív.");

                model.addAttribute("pageTitle",
                        isNew ? "Új felhasználó" : "Felhasználó szerkesztése");

                addFormAttributes(model);
                return "admin/user-form";
            }

            user.setAssignedDoctor(doctor);
        }

        userRepository.save(user);

        ra.addFlashAttribute(
                "message",
                isNew
                        ? "A felhasználó sikeresen létrehozva."
                        : "A felhasználó adatai sikeresen módosítva."
        );

        return "redirect:/admin/users";
    }

    // FELHASZNÁLÓ INAKTIVÁLÁSA

    @PostMapping("/{id}/delete")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("error", "A felhasználó nem található.");
            return "redirect:/admin/users";
        }

        user.setActive(false);
        userRepository.save(user);

        ra.addFlashAttribute(
                "message",
                "A felhasználó sikeresen inaktiválva."
        );

        return "redirect:/admin/users";
    }

    // ŰRLAPHOZ SZÜKSÉGES ADATOK

    private void addFormAttributes(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("specializations", specializationRepository.findAll());
        model.addAttribute(
                "doctors",
                userRepository.findByRole_NameAndActiveTrue("DOCTOR")
        );
    }
}
