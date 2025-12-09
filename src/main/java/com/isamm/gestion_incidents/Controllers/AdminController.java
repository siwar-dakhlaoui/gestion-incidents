/*package com.isamm.gestion_incidents.Controllers;

import ch.qos.logback.core.model.Model;
import com.isamm.gestion_incidents.Models.Role;
import com.isamm.gestion_incidents.Security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class AdminController {

    @Autowired
    private UserService userService;

    // Afficher le formulaire de création d'utilisateur
    @GetMapping("/create-user")
    public String showCreateUserForm(Model model) {
        model.addAttribute("userForm", new UserCreationForm());
        // Ajouter la liste des rôles disponibles (sauf CITOYEN peut-être, selon les besoins)
        model.addAttribute("roles", Role.values()); // Assurez-vous que Role est une enum
        return "admin/create-user";
    }

    // Traiter la création d'utilisateur
    @PostMapping("/create-user")
    public String createUser(@ModelAttribute("userForm") UserCreationForm userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/create-user";
        }

        // Appeler le service pour créer l'utilisateur
        userService.createUserByAdmin(userForm);
        return "redirect:/admin/users?success";
    }

    // Afficher la liste des utilisateurs (optionnel)
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
}
*/