package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.Security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public UserDetailsService userDetailsService() {
        return null;
    }

}
