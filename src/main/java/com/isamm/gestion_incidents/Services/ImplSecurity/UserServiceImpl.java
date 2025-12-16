package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Ajoute ROLE_ prefix à ton rôle
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole())
            );

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        };
    }
  /*  @Override
    public User inscrireCitoyen(User user) {
        // ... implementation
        return null;
    }

    public void createUserByAdmin(UserCreationForm userForm) {
        // Vérifier que l'email n'existe pas déjà
        if (userRepository.findByEmail(userForm.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setNom(userForm.getNom());
        user.setEmail(userForm.getEmail());
        user.setRole(userForm.getRole());

        // Générer un mot de passe aléatoire
        String plainPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        user.setPassword(encodedPassword);

        // Désactiver la vérification d'email pour les comptes créés par admin, ou envoyer un email de vérification ?
        // Ici, on considère que l'admin crée des comptes pour des collègues, donc on peut marquer l'email comme vérifié.
        user.setEnabled(true); // Si vous avez un champ enabled

        userRepository.save(user);

        // Envoyer un email à l'utilisateur avec son mot de passe temporaire
        emailService.sendAccountCreationEmail(user.getEmail(), plainPassword);
    }

    private String generateRandomPassword() {
        // Logique pour générer un mot de passe aléatoire
        // Par exemple, utiliser Apache Commons Lang RandomStringUtils ou une méthode maison
        return RandomStringUtils.randomAlphanumeric(10);
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    @Override
    public User updateUser(Long id, User user) {
        // ... implementation
        return null;
    }*/
}

