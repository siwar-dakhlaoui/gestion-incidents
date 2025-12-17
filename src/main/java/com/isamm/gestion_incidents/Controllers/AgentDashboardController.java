package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.Repositories.IncidentRepository;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agent")
@PreAuthorize("hasRole('AGENT_MUNICIPAL')")
@RequiredArgsConstructor
public class AgentDashboardController {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Ajouter des statistiques pour l'agent
        String username = "Agent Municipal"; // Récupérer depuis l'authentification

        model.addAttribute("username", username);
        model.addAttribute("role", "AGENT_MUNICIPAL");

        // Exemple de statistiques
        model.addAttribute("assignedIncidents", 5);
        model.addAttribute("inProgressIncidents", 2);
        model.addAttribute("completedIncidents", 3);
        model.addAttribute("urgentIncidents", 1);
        model.addAttribute("completionRate", "75%");
        model.addAttribute("avgResponseTime", "2h");
        model.addAttribute("satisfactionScore", "4.2/5");

        return "agent/dashboard";
    }

    @GetMapping("/incidents")
    public String incidents() {
        return "agent/incidents";
    }

    @GetMapping("/assignments")
    public String assignments() {
        return "agent/assignments";
    }

    @GetMapping("/map")
    public String map() {
        return "agent/map";
    }

    @GetMapping("/reports")
    public String reports() {
        return "agent/reports";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }
}