package com.isamm.gestion_incidents.Configuration;

import com.isamm.gestion_incidents.Security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Autoriser les pages Thymeleaf publiques
                        .requestMatchers(
                                "/user/signin",
                                "/user/signup",
                                "/user/signin/**",
                                "/user/signup/**",
                                "/auth/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()
                        // Autoriser l'accès aux templates
                        .requestMatchers(
                                "/templates/**",
                                "/static/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Configuration pour Thymeleaf (avec session)
                .formLogin(form -> form
                        .loginPage("/user/signin")  // Page de login personnalisée
                        .loginProcessingUrl("/user/signin")  // URL de traitement
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/user/signin?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/user/signin?logout=true")
                        .permitAll()
                )
                // Ajouter le filtre JWT avant le filtre d'authentification
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Session management pour supporter à la fois API et Thymeleaf
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Changé de STATELESS
                        .maximumSessions(1)
                        .expiredUrl("/user/signin?expired=true")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}