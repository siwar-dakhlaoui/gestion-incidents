package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.DTO.request.SignInRequest;
import com.isamm.gestion_incidents.DTO.request.SignUpRequest;
import com.isamm.gestion_incidents.DTO.response.JwtAuthenticationResponse;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup (@RequestBody
                                                                 SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.SignUp(request));
    }
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse>signIn(@RequestBody

                SignInRequest request,
                HttpServletResponse response){
            JwtAuthenticationResponse jwtResponse=authenticationService.SignIn(request);

            response.setHeader( "Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Authorization", "Bearer " + jwtResponse.getAccessToken());
            return ResponseEntity.ok(jwtResponse);

        }



}
