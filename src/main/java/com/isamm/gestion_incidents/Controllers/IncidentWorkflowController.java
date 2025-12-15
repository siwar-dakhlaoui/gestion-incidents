package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.Security.IncidentWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class IncidentWorkflowController {
    private final IncidentWorkflowService incidentWorkflowService;

    @PostMapping("/{incidentId}/assign/{agentId}")
    public String assign(@PathVariable Long incidentId, @PathVariable Long agentId) {
        incidentWorkflowService.assignToAgent(incidentId, agentId);
        return "redirect:/incidents";
    }

    @PostMapping("/{incidentId}/start")
    public String startResolution(@PathVariable Long incidentId) {
        incidentWorkflowService.startResolution(incidentId);
        return "redirect:/incidents";
    }

    @PostMapping("/{incidentId}/resolve")
    public String markResolved(@PathVariable Long incidentId) {
        incidentWorkflowService.markResolved(incidentId);
        return "redirect:/incidents";
    }

    @PostMapping("/{incidentId}/close")
    public String close(@PathVariable Long incidentId) {
        incidentWorkflowService.closeIncident(incidentId);
        return "redirect:/incidents";
    }
}
