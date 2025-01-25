
package com.webalk.webapp.controller;

import com.webalk.webapp.entity.Role;
import com.webalk.webapp.entity.User;
import com.webalk.webapp.repository.RoleRepository;
import com.webalk.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/add-user")
    public String addUserForm(Model model) {
        List<Role> roles = roleRepository.findAll();
        if (roles == null || roles.isEmpty()) { 
            throw new IllegalStateException("Nincsenek elérhető szerepkörök az adatbázisban.");
        }
        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);
        return "add-user";
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute User user, @RequestParam("roleId") Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Felhasználó nem található ezzel az ID-val: " + roleId)); 
        user.setRole(role);
        userService.saveUser(user);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Felhasználó nem található ezzel az ID-val: " + id)); 
        List<Role> roles = roleRepository.findAll();
        if (roles == null || roles.isEmpty()) { 
            throw new IllegalStateException("Nincsenek elérhető szerepkörök a szerkesztéshez");
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "edit-user";
    }

    @PostMapping("/edit-user/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam("roleId") Long roleId) {
        User existingUser = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Felhasználó nem található ezzel az ID-val: " + id));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Nincsenek elérhető szerepkörök a szerkesztéshez " + roleId));
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(role);
        userService.saveUser(existingUser);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) { 
            throw new IllegalArgumentException("Nem lehet törölni a felhasználót. Felhasználó ezzel az ID-val: " + id + " nem létezik.");
        }
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }
}
