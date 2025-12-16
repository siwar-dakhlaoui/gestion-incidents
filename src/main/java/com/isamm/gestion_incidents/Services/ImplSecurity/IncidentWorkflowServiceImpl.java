package com.isamm.gestion_incidents.Services.ImplSecurity;

import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Exception.ResourceNotFoundException;
import com.isamm.gestion_incidents.Models.Incident;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.IncidentRepository;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Security.IncidentWorkflowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentWorkflowServiceImpl implements IncidentWorkflowService {

   private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    private Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident introuvable"));
    }

    private void validateTransition(IncidentStatus current, IncidentStatus next) {
        switch (current) {
            case SIGNALE:
                if (next != IncidentStatus.PRIS_EN_CHARGE)
                    throw new IllegalStateException("Transition impossible");
                break;
            case PRIS_EN_CHARGE:
                if (next != IncidentStatus.EN_RESOLUTION)
                    throw new IllegalStateException("Transition impossible");
                break;
            case EN_RESOLUTION:
                if (next != IncidentStatus.RESOLU)
                    throw new IllegalStateException("Transition impossible");
                break;
            case RESOLU:
                if (next != IncidentStatus.CLOTURE)
                    throw new IllegalStateException("Transition impossible");
                break;
            case CLOTURE:
                throw new IllegalStateException("Incident déjà clôturé");
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public void assignToAgent(Long incidentId, Long agentId) {
        Incident incident = getIncident(incidentId);
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable"));
        validateTransition(incident.getStatus(), IncidentStatus.PRIS_EN_CHARGE);
        incident.setAgent(agent);
        incident.setStatus(IncidentStatus.PRIS_EN_CHARGE);
        incidentRepository.save(incident);
    }

    @Override
    @PreAuthorize("hasRole('AGENT_MUNICIPAL')")
    public void startResolution(Long incidentId) {
        Incident incident = getIncident(incidentId);
        validateTransition(incident.getStatus(), IncidentStatus.EN_RESOLUTION);
        incident.setStatus(IncidentStatus.EN_RESOLUTION);
        incidentRepository.save(incident);
    }

    @Override
    @PreAuthorize("hasRole('AGENT_MUNICIPAL')")
    public void markResolved(Long incidentId) {
        Incident incident = getIncident(incidentId);
        validateTransition(incident.getStatus(), IncidentStatus.RESOLU);
        incident.setStatus(IncidentStatus.RESOLU);
        incident.setResolvedAt(LocalDateTime.now());
        incidentRepository.save(incident);
    }

    @Override
    @PreAuthorize("hasRole('CITOYEN')")
    public void closeIncident(Long incidentId) {
        Incident incident = getIncident(incidentId);
        validateTransition(incident.getStatus(), IncidentStatus.CLOTURE);
        incident.setStatus(IncidentStatus.CLOTURE);
        incidentRepository.save(incident);
    }
}
