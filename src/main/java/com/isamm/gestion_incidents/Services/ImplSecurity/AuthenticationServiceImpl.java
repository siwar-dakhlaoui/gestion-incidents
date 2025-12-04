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
import org.springframework.beans.factory.annotation.Autowired;
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
    private final EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;
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
        emailVerificationService.sendVerificationEmail(user);

        var jwtToken = jwtService.generateToken(user);


        return JwtAuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .role(user.getRole().name())
                .isVerified(false)
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

        // check if user is verified
        if (!user.isVerified()){
            throw new RuntimeException("Account not verified"+
                    "Please check your email for verification link");
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
