package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.DTO.request.SignInRequest;
import com.isamm.gestion_incidents.DTO.request.SignUpRequest;
import com.isamm.gestion_incidents.DTO.response.JwtAuthenticationResponse;
import com.isamm.gestion_incidents.Enum.Role;
import com.isamm.gestion_incidents.Security.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthViewController {
    private final AuthenticationService authenticationService;

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "user/signup";
    }

    @GetMapping("/signin")
    public String showSigninPage(Model model) {
        model.addAttribute("signInRequest", new SignInRequest());
        return "user/signin";
    }

    @PostMapping("/signup")
    public String signupForm(@ModelAttribute SignUpRequest request,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        try {
            // Toujours CITOYEN pour l'inscription publique
            request.setRole(Role.CITOYEN);

            authenticationService.SignUp(request);

            // Rediriger avec message de succès
            return "redirect:/user/signin?success=Compte créé avec succès! Veuillez vérifier votre email.";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("signUpRequest", request);
            return "user/signup";
        }
    }

    @PostMapping("/signin")
    public String signinForm(@ModelAttribute SignInRequest request,
                             Model model,
                             HttpServletResponse httpResponse) {
        try {
            JwtAuthenticationResponse jwt = authenticationService.SignIn(request);

            // Vérifier si l'email est vérifié
            if (!jwt.isVerified()) {
                model.addAttribute("error", "Veuillez vérifier votre email avant de vous connecter.");
                model.addAttribute("signInRequest", request);
                return "user/signin";
            }

            // Stocker le token dans un cookie
            Cookie tokenCookie = new Cookie("token", jwt.getAccessToken());
            tokenCookie.setHttpOnly(true);
            tokenCookie.setSecure(false);
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(24 * 60 * 60);
            httpResponse.addCookie(tokenCookie);

            // Stocker d'autres infos
            Cookie userIdCookie = new Cookie("userId", jwt.getUserId().toString());
            userIdCookie.setPath("/");
            userIdCookie.setMaxAge(24 * 60 * 60);
            httpResponse.addCookie(userIdCookie);

            Cookie roleCookie = new Cookie("role", jwt.getRole());
            roleCookie.setPath("/");
            roleCookie.setMaxAge(24 * 60 * 60);
            httpResponse.addCookie(roleCookie);

            // Rediriger selon le rôle
            switch (jwt.getRole()) {
                case "ADMINISTRATEUR":
                    return "redirect:/admin/dashboard";
                case "AGENT_MUNICIPAL":
                    return "redirect:/agent/dashboard";
                default: // CITOYEN
                    return "redirect:/citoyen/dashboard";
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("signInRequest", request);
            return "user/signin";
        }
    }

    // Pages dashboard selon les rôles
    @GetMapping("/citoyen/dashboard")
    public String showCitoyenDashboard() {
        return "citoyen/dashboard";
    }

    @GetMapping("/agent/dashboard")
    public String showAgentDashboard() {
        return "agent/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Supprimer les cookies
        String[] cookieNames = {"token", "userId", "role"};
        for (String cookieName : cookieNames) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        return "redirect:/user/signin?logout=true";
    }
}