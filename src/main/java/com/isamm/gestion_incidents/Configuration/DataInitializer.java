package com.isamm.gestion_incidents.Configuration;

import com.isamm.gestion_incidents.Enum.Role;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@test.com").isEmpty()) {
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("Admin123!")) // mot de passe
                    .role(Role.ADMINISTRATEUR)
                    .isVerified(true)
                    .isEnabled(true)
                    .dateInscription(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println("Admin créé : admin@test.com / mot de passe : Admin123!");
        }
    }
}
