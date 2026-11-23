
package com.SFM.KIP.controller;

import com.SFM.KIP.entity.Room;
import com.SFM.KIP.repository.DepartmentRepository;
import com.SFM.KIP.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", roomRepository.findAll());
        return "admin/rooms";
    }

    @GetMapping("/new")
    public String newRoom(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("pageTitle", "Új szoba");

        return "admin/room-form";
    }

    @GetMapping("/{id}/edit")
    public String editRoom(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ) {
        Room room = roomRepository.findById(id).orElse(null);

        if (room == null) {
            ra.addFlashAttribute("error", "A szoba nem található.");
            return "redirect:/admin/rooms";
        }

        model.addAttribute("room", room);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("pageTitle", "Szoba szerkesztése");

        return "admin/room-form";
    }

    @PostMapping
    public String saveRoom(
            @ModelAttribute Room room,
            @RequestParam Long departmentId,
            RedirectAttributes ra
    ) {
        var department = departmentRepository.findById(departmentId)
                .orElse(null);

        if (department == null) {
            ra.addFlashAttribute("error", "A kiválasztott osztály nem található.");
            return "redirect:/admin/rooms";
        }

        room.setDepartment(department);
        roomRepository.save(room);

        ra.addFlashAttribute("message", "A szoba sikeresen mentve.");

        return "redirect:/admin/rooms";
    }

    @PostMapping("/{id}/delete")
    public String deleteRoom(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {
        if (!roomRepository.existsById(id)) {
            ra.addFlashAttribute("error", "A szoba nem található.");
            return "redirect:/admin/rooms";
        }

        roomRepository.deleteById(id);

        ra.addFlashAttribute("message", "A szoba sikeresen törölve.");

        return "redirect:/admin/rooms";
    }
}
