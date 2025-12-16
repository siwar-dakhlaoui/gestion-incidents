package com.isamm.gestion_incidents.Services;

import com.isamm.gestion_incidents.DTO.request.IncidentRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IncidentService {
    void declarerIncident(
            IncidentRequest request,
            MultipartFile[] photos,
            String emailCitoyen
    );
}
