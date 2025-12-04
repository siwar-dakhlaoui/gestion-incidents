package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.DTO.request.SignInRequest;
import com.isamm.gestion_incidents.DTO.request.SignUpRequest;
import com.isamm.gestion_incidents.DTO.response.JwtAuthenticationResponse;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.AuthenticationService;
import com.isamm.gestion_incidents.Security.EmailVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

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
    @GetMapping("/verify")
    public void verifyAccount(
            @RequestParam String token,
            HttpServletResponse response) throws IOException {
        try {
            User user=userRepository.findByVerificationToken(token)
                    .orElseThrow(()-> new RuntimeException("Invalid verification token"));
            if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now()))
            {
                throw new RuntimeException("Verification link has expired");
            }

            user.setVerified(true);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);

            //Redirect to frontend LOGIN page (not error page)
            String redirectUrl= "http://localhost:4200/pages/login" +"?verified=true"
                    + "&email="+ URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)+
                    "&message=Account verified successfully!";
            response.sendRedirect(redirectUrl);
        }
        catch (RuntimeException e) {
            String redirectUrl= "http://localhost:4200/pages/login"+
                    "?verified=false"+
                    "&error="+URLEncoder.encode((e.getMessage()),StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);

        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam String email){
        try {
            User user=userRepository.findByEmail(email)
                    .orElseThrow(()-> new RuntimeException("user not found with email: "+email));
            if (user.isVerified()){
                return ResponseEntity.ok("Account is already verified");
            }

            //generate new token and expiry
            String newToken= UUID.randomUUID().toString();
            user.setVerificationToken(newToken);
            user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(1));
            userRepository.save(user);

            // resend email
            String verificationUrl = "http://localhost:8087/api/v1/auth/verify";

            String verificationLink = verificationUrl + "?token="+ newToken;
            emailVerificationService.sendVerificationEmail(user);
            return ResponseEntity.ok(Map.of(
                    "status","success",
                    "message","verificationemail resent successfully"));


        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status","error",
                            "message","Failed to resend verification email: "+e.getMessage()));
        }
    }



}
