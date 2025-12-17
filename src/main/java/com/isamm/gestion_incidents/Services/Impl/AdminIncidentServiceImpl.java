package com.isamm.gestion_incidents.Services.Impl;

import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Enum.Role;
import com.isamm.gestion_incidents.Models.Incident;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.IncidentRepository;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Services.AdminIncidentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminIncidentServiceImpl implements AdminIncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    @Override
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    @Override
    public List<User> getAllAgents() {
        return userRepository.findByRole(Role.AGENT_MUNICIPAL);
    }

    // ✅ ASSIGNATION AGENT + STATUT AUTO
    @Override
    public void assignerAgent(Long incidentId, Long agentId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident introuvable"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        incident.setAgentAssigne(agent);
        incident.setStatut(IncidentStatus.PRIS_EN_CHARGE);
        incident.setDatePriseEnCharge(LocalDateTime.now());

        incidentRepository.save(incident);
    }

    // ✅ CHANGEMENT DE STATUT MANUEL
    @Override
    public void changerStatut(Long incidentId, IncidentStatus statut) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident introuvable"));

        incident.setStatut(statut);

        if (statut == IncidentStatus.RESOLU) {
            incident.setDateResolution(LocalDateTime.now());
        }

        incidentRepository.save(incident);
    }
}
