package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.DTO.request.AdminCreateUserRequest;
import com.isamm.gestion_incidents.Enum.Role;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUserByAdmin(AdminCreateUserRequest request) {
        log.info("Début création utilisateur par admin: email={}", request.getEmail());

        // Validation
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalStateException("L'email est requis");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalStateException("Le mot de passe doit contenir au moins 6 caractères");
        }

        if (request.getRole() == null) {
            throw new IllegalStateException("Le rôle est requis");
        }

        if (request.getRole() == Role.CITOYEN) {
            throw new IllegalStateException("Les administrateurs ne peuvent créer que des AGENT_MUNICIPAL ou ADMINISTRATEUR");
        }

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Un utilisateur avec l'email " + request.getEmail() + " existe déjà");
        }

        try {
            // Extraire le nom à partir de l'email (ex: agent@municipal.com -> "Agent")
            String baseName = request.getEmail().split("@")[0];
            String firstName = capitalize(baseName);
            String lastName = request.getRole().toString();

            log.info("Création de l'utilisateur: firstName={}, lastName={}, role={}",
                    firstName, lastName, request.getRole());

            // Créer l'utilisateur avec TOUS les champs requis
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .firstName(firstName) // Champ requis!
                    .lastName(lastName)   // Champ requis!
                    .isVerified(true)     // Vérifié automatiquement
                    .isEnabled(true)      // Activé automatiquement
                    .build();

            // Sauvegarder

            User savedUser = userRepository.save(user);
            log.info("Utilisateur créé avec succès: ID={}, Email={}", savedUser.getId(), savedUser.getEmail());

        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage(), e);
            throw new IllegalStateException("Erreur lors de la création: " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return "Utilisateur";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}