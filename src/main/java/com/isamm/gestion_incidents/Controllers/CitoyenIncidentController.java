package com.isamm.gestion_incidents.Controllers;

import com.isamm.gestion_incidents.DTO.request.IncidentRequest;
import com.isamm.gestion_incidents.Enum.IncidentCategory;
import com.isamm.gestion_incidents.Services.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/citoyen")
@RequiredArgsConstructor
public class CitoyenIncidentController {

    private final IncidentService incidentService;


    @GetMapping("/declarer")
    public String showDeclarationForm(Model model) {
        model.addAttribute("incident", new IncidentRequest());
        model.addAttribute("categories", IncidentCategory.values());
        return "citoyen/declarer-incident";
    }

    @PostMapping("/declarer")
    public String declarerIncident(
            @Valid @ModelAttribute("incident") IncidentRequest incident,
            BindingResult result,
            @RequestParam("photos") MultipartFile[] photos,
            Authentication authentication,
            RedirectAttributes redirect
    ) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("error", "Formulaire invalide");
            return "redirect:/citoyen/declarer";
        }

        incidentService.declarerIncident(
                incident,
                photos,
                authentication.getName()
        );

        // MESSAGE DE SUCCÈS
        redirect.addFlashAttribute(
                "successMessage",
                "Incident déclaré avec succès"
        );

        // REDIRECTION VERS DASHBOARD
        return "redirect:/citoyen/dashboard";

    }
    @GetMapping("/incidents")
    public String mesIncidents(Model model, Authentication authentication) {

        String email = authentication.getName();

        model.addAttribute(
                "incidents",
                incidentService.getIncidentsByCitoyen(email)
        );

        return "citoyen/incidents";
    }


}
