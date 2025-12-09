package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.DTO.request.SignInRequest;
import com.isamm.gestion_incidents.DTO.request.SignUpRequest;
import com.isamm.gestion_incidents.DTO.response.JwtAuthenticationResponse;
import com.isamm.gestion_incidents.Models.Role;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.AuthenticationService;
import com.isamm.gestion_incidents.Security.EmailService;
import com.isamm.gestion_incidents.Security.EmailVerificationService;
import com.isamm.gestion_incidents.Security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public JwtAuthenticationResponse SignUp(SignUpRequest request) {
        log.info("Début inscription pour email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Email déjà utilisé: {}", request.getEmail());
            throw new RuntimeException("Email already in use");
        }

        try {
            Role role = request.getRole() != null ? request.getRole() : Role.CITOYEN;

            // CRÉER LE TOKEN DE VÉRIFICATION
            String verificationToken = UUID.randomUUID().toString();

            // CORRECTION ICI : Ajouter TOUS les champs obligatoires
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .isVerified(false) // CHAMP MANQUANT - OBLIGATOIRE
                    .verificationToken(verificationToken) // CHAMP MANQUANT - OBLIGATOIRE
                    .verificationTokenExpiry(LocalDateTime.now().plusDays(1)) // CHAMP MANQUANT - OBLIGATOIRE
                    .dateInscription(LocalDateTime.now())
                    .isEnabled(true)
                    .build();

            log.info("Utilisateur créé avec vérificationToken: {}", verificationToken);

            User savedUser = userRepository.save(user);
            log.info("Utilisateur sauvegardé avec ID: {}", savedUser.getId());

            // Envoyer l'email de vérification
            emailVerificationService.sendVerificationEmail(savedUser);
            log.info("Email de vérification envoyé");

            var jwtToken = jwtService.generateToken(savedUser);

            return JwtAuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .userId(savedUser.getId())
                    .role(savedUser.getRole().name())
                    .isVerified(savedUser.isVerified())
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: ", e);
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    @Override
    public JwtAuthenticationResponse SignIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email or Password"));

        // Vérifier si l'utilisateur est vérifié
        if (!user.isVerified()){
            log.warn("Tentative de connexion avec compte non vérifié: {}", user.getEmail());
            throw new RuntimeException("Account not verified. Please check your email for verification link");
        }

        var jwtToken = jwtService.generateToken(user);


        return JwtAuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .role(user.getRole().name())
                .isVerified(user.isVerified())
                .build();
    }
}