package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.DTO.request.SignInRequest;
import com.isamm.gestion_incidents.DTO.request.SignUpRequest;
import com.isamm.gestion_incidents.DTO.response.JwtAuthenticationResponse;
import com.isamm.gestion_incidents.Models.Role;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.AuthenticationService;
import com.isamm.gestion_incidents.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public JwtAuthenticationResponse SignUp(SignUpRequest request) {


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.CITOYEN;
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);


        return JwtAuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public JwtAuthenticationResponse SignIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        //fetch the user from DB

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email or Password"));



        var jwtToken = jwtService.generateToken(user);

        return JwtAuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .role(user.getRole().name())
                .build();

    }


}
