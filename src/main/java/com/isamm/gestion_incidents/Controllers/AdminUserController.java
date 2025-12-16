package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.DTO.request.AdminCreateUserRequest;
import com.isamm.gestion_incidents.Enum.Role;
import com.isamm.gestion_incidents.Security.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATEUR')")
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/create")
    public String createUserForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new AdminCreateUserRequest());
        }
        model.addAttribute("roles", List.of(Role.AGENT_MUNICIPAL, Role.ADMINISTRATEUR));
        return "admin/create-user";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") AdminCreateUserRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        log.info("Requête POST reçue pour créer un utilisateur");
        log.info("Email: {}, Role: {}, Password present: {}",
                request.getEmail(), request.getRole(),
                request.getPassword() != null);

        // Validation
        if (bindingResult.hasErrors()) {
            log.error("Erreurs de validation: {}", bindingResult.getAllErrors());
            model.addAttribute("roles", List.of(Role.AGENT_MUNICIPAL, Role.ADMINISTRATEUR));
            return "admin/create-user";
        }

        try {
            // Validation supplémentaire
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                bindingResult.rejectValue("email", "email.required", "L'email est requis");
                model.addAttribute("roles", List.of(Role.AGENT_MUNICIPAL, Role.ADMINISTRATEUR));
                return "admin/create-user";
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                bindingResult.rejectValue("password", "password.length",
                        "Le mot de passe doit contenir au moins 6 caractères");
                model.addAttribute("roles", List.of(Role.AGENT_MUNICIPAL, Role.ADMINISTRATEUR));
                return "admin/create-user";
            }

            if (request.getRole() == null) {
                bindingResult.rejectValue("role", "role.required", "Le rôle est requis");
                model.addAttribute("roles", List.of(Role.AGENT_MUNICIPAL, Role.ADMINISTRATEUR));
                return "admin/create-user";
            }

            log.info("Appel du service pour créer l'utilisateur: {}", request.getEmail());
            adminUserService.createUserByAdmin(request);

            log.info("Utilisateur créé avec succès: {}", request.getEmail());
            redirectAttributes.addFlashAttribute("success",
                    "Utilisateur " + request.getEmail() + " créé avec succès !");

            return "redirect:/admin/dashboard";

        } catch (IllegalStateException e) {
            log.error("Erreur de création: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("user", request);
            return "redirect:/admin/users/create";

        } catch (Exception e) {
            log.error("Erreur inattendue: ", e);
            redirectAttributes.addFlashAttribute("error",
                    "Erreur technique: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            redirectAttributes.addFlashAttribute("user", request);
            return "redirect:/admin/users/create";
        }
    }

    @GetMapping
    public String listUsers() {
        return "admin/users-list";
    }
}