package com.isamm.gestion_incidents.Services;

import com.isamm.gestion_incidents.DTO.request.IncidentRequest;
import com.isamm.gestion_incidents.Models.Incident;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IncidentService {
    void declarerIncident(
            IncidentRequest request,
            MultipartFile[] photos,
            String emailCitoyen
    );
    List<Incident> getIncidentsByCitoyen(String email);

}
