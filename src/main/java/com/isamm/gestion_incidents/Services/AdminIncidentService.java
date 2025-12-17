package com.isamm.gestion_incidents.Services;

import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Models.Incident;
import com.isamm.gestion_incidents.Models.User;

import java.util.List;

public interface AdminIncidentService {

    List<Incident> getAllIncidents();

    List<User> getAllAgents();

    void assignerAgent(Long incidentId, Long agentId);

    void changerStatut(Long incidentId, IncidentStatus statut);
}
