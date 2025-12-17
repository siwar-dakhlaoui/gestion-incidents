package com.isamm.gestion_incidents.Services.Impl;

import com.isamm.gestion_incidents.Models.Incident;
import com.isamm.gestion_incidents.Repositories.IncidentRepository;
import com.isamm.gestion_incidents.Services.AdminIncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminIncidentServiceImpl implements AdminIncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
}
