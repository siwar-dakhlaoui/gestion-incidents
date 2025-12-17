package com.isamm.gestion_incidents.Services.Impl;

import com.isamm.gestion_incidents.DTO.request.UpdateProfileRequest;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.UpdateProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateProfileServiceImpl implements UpdateProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User updateUserProfile(String userEmail, UpdateProfileRequest request) {
        log.info("Mise à jour du profil pour l'utilisateur: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + userEmail));

        // Vérifier si l'email a changé
        boolean emailChanged = !user.getEmail().equals(request.getEmail());

        if (emailChanged) {
            // Vérifier que le nouvel email n'est pas déjà utilisé par un autre utilisateur
            if (isEmailAvailable(request.getEmail(), user.getId())) {
                user.setEmail(request.getEmail());
            } else {
                throw new RuntimeException("Cet email est déjà utilisé par un autre utilisateur");
            }
        }

        // Mettre à jour les informations de base
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Mettre à jour les informations optionnelles

        // Sauvegarder
        User updatedUser = userRepository.save(user);
        log.info("Profil mis à jour avec succès pour: {}", updatedUser.getEmail());

        return updatedUser;
    }

    @Override
    @Transactional
    public User updatePassword(String userEmail, String currentPassword, String newPassword) {
        log.info("Changement de mot de passe pour: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        // Vérifier que le nouveau mot de passe est différent
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));

        User updatedUser = userRepository.save(user);
        log.info("Mot de passe changé avec succès pour: {}", updatedUser.getEmail());

        return updatedUser;
    }

    @Override
    public boolean isEmailAvailable(String email, Long currentUserId) {
        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(currentUserId))
                .orElse(true); // Email disponible si non trouvé
    }
}