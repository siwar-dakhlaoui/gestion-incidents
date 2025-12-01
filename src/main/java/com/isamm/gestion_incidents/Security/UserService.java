package com.isamm.gestion_incidents.Security;

import com.isamm.gestion_incidents.Models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    User inscrireCitoyen(User user);

    User creerCompteParAdmin(User user);

    User findByEmail(String email);

    User updateUser(Long id, User user);
}
