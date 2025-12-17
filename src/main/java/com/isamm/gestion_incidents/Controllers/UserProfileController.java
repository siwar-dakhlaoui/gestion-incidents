package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.DTO.request.UpdateProfileRequest;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.UpdateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserRepository userRepository;
    private final UpdateProfileService updateProfileService;

    // ============ AFFICHAGE DU PROFIL ============
    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        log.info("=== Accès à la page de profil ===");

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/user/signin";
            }

            String email = authentication.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                model.addAttribute("user", user);
                model.addAttribute("role", user.getRole() != null ? user.getRole().name() : "Non défini");

                // Préparer le formulaire de mise à jour
                if (!model.containsAttribute("updateRequest")) {
                    UpdateProfileRequest updateRequest = new UpdateProfileRequest();
                    updateRequest.setFirstName(user.getFirstName());
                    updateRequest.setLastName(user.getLastName());
                    updateRequest.setEmail(user.getEmail());
                    model.addAttribute("updateRequest", updateRequest);
                }

                return "user/profile";
            } else {
                return "redirect:/user/signin?error=Utilisateur non trouvé";
            }

        } catch (Exception e) {
            log.error("Erreur: ", e);
            return "redirect:/user/signin?error=" + e.getMessage();
        }
    }

    // ============ ÉDITION DU PROFIL ============
    @GetMapping("/profile/edit")
    public String showEditForm(Model model, Authentication authentication,
                               @ModelAttribute("updateRequest") UpdateProfileRequest updateRequest) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/signin";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Si le formulaire n'est pas pré-rempli (première visite)
        if (updateRequest == null || updateRequest.getFirstName() == null) {
            updateRequest = new UpdateProfileRequest();
            updateRequest.setFirstName(user.getFirstName());
            updateRequest.setLastName(user.getLastName());
            updateRequest.setEmail(user.getEmail());

        }

        model.addAttribute("user", user);
        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("role", user.getRole().name());

        return "user/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateRequest") UpdateProfileRequest updateRequest,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        log.info("Tentative de mise à jour du profil");

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/signin";
        }

        String email = authentication.getName();

        // Validation
        if (bindingResult.hasErrors()) {
            log.error("Erreurs de validation: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateRequest", updateRequest);
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs dans le formulaire");
            return "redirect:/user/profile/edit";
        }

        try {
            // Utiliser le service pour mettre à jour
            User updatedUser = updateProfileService.updateUserProfile(email, updateRequest);

            // Message de succès
            redirectAttributes.addFlashAttribute("success",
                    "Profil mis à jour avec succès !");

            // Si l'email a changé, déconnecter l'utilisateur
            if (!email.equals(updatedUser.getEmail())) {
                redirectAttributes.addFlashAttribute("info",
                        "Votre email a été modifié. Veuillez vous reconnecter.");
                return "redirect:/user/logout";
            }

            return "redirect:/user/profile";

        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du profil: ", e);
            redirectAttributes.addFlashAttribute("updateRequest", updateRequest);
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/user/profile/edit";
        }
    }

    // ============ CHANGEMENT DE MOT DE PASSE ============
    @GetMapping("/profile/change-password")
    public String showChangePasswordForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/signin";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        model.addAttribute("user", user);
        model.addAttribute("role", user.getRole().name());

        return "user/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/signin";
        }

        String email = authentication.getName();

        try {
            // Validation basique
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error",
                        "Les nouveaux mots de passe ne correspondent pas");
                return "redirect:/user/profile/change-password";
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error",
                        "Le mot de passe doit contenir au moins 6 caractères");
                return "redirect:/user/profile/change-password";
            }

            // Utiliser le service pour changer le mot de passe
            updateProfileService.updatePassword(email, currentPassword, newPassword);

            redirectAttributes.addFlashAttribute("success",
                    "Mot de passe changé avec succès !");

            return "redirect:/user/profile";

        } catch (Exception e) {
            log.error("Erreur lors du changement de mot de passe: ", e);
            redirectAttributes.addFlashAttribute("error",
                    "Erreur: " + e.getMessage());
            return "redirect:/user/profile/change-password";
        }
    }
}