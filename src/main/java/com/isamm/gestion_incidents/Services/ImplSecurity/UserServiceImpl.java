package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            log.debug("Loading user details for: {}", username);

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", username);
                        return new UsernameNotFoundException("User not found with email: " + username);
                    });

            // IMPORTANT: Format correct pour Spring Security: ROLE_XXX
            String role = user.getRole().name();
            String authority = "ROLE_" + role;

            log.debug("User found - Email: {}, Role: {}, Authority: {}",
                    user.getEmail(), role, authority);

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(authority)
            );

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(),
                    true,  // accountNonExpired
                    true,  // credentialsNonExpired
                    true,  // accountNonLocked
                    authorities
            );
        };
    }

    // Méthode supplémentaire pour debug
    public void debugUserRoles(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            log.info("DEBUG User Roles - Email: {}, Role: {}, Authority: ROLE_{}",
                    user.getEmail(), user.getRole(), user.getRole());
        } catch (Exception e) {
            log.error("Debug error: {}", e.getMessage());
        }
    }
}