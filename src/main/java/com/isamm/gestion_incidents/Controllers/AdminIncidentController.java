package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Services.AdminIncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/incidents")
@RequiredArgsConstructor
public class AdminIncidentController {

    private final AdminIncidentService adminIncidentService;

    @GetMapping
    public String listIncidents(Model model) {
        model.addAttribute("incidents", adminIncidentService.getAllIncidents());
        model.addAttribute("agents", adminIncidentService.getAllAgents());
        model.addAttribute("statuts", IncidentStatus.values());
        return "admin/incidents";
    }

    // ✅ ASSIGNER AGENT → STATUT PASSE AUTOMATIQUEMENT À PRIS_EN_CHARGE
    @PostMapping("/{id}/assign")
    public String assignerAgent(
            @PathVariable Long id,
            @RequestParam Long agentId
    ) {
        adminIncidentService.assignerAgent(id, agentId);
        return "redirect:/admin/incidents";
    }

    // ✅ CHANGER STATUT MANUELLEMENT
    @PostMapping("/{id}/statut")
    public String changerStatut(
            @PathVariable Long id,
            @RequestParam IncidentStatus statut
    ) {
        adminIncidentService.changerStatut(id, statut);
        return "redirect:/admin/incidents";
    }
}
