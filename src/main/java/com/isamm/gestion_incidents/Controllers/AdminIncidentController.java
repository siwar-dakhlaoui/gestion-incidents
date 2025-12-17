package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.Services.AdminIncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/incidents")
@RequiredArgsConstructor
public class AdminIncidentController {

    private final AdminIncidentService adminIncidentService;

    @GetMapping
    public String listIncidents(Model model) {
        model.addAttribute("incidents", adminIncidentService.getAllIncidents());
        return "admin/incidents";
    }
}
