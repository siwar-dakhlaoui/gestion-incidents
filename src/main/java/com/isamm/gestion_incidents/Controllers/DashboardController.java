package com.isamm.gestion_incidents.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/dashboard")
    public String redirectToDashboard(HttpServletRequest request) {
        // Logique de redirection selon le rôle
        // Vous pouvez récupérer le rôle depuis les cookies ou la session
        return "redirect:/user/citoyen/dashboard";
    }

    @GetMapping("/citoyen/dashboard")
    @PreAuthorize("hasRole('CITOYEN')")
    public String citoyenDashboard() {
        return "citoyen/dashboard";
    }

    @GetMapping("/agent/dashboard")
    @PreAuthorize("hasRole('AGENT_MUNICIPAL')")
    public String agentDashboard() {
        return "agent/dashboard";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}