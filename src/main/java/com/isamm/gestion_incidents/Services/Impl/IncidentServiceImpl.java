package com.isamm.gestion_incidents.Services.Impl;

import com.isamm.gestion_incidents.DTO.request.IncidentRequest;
import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Models.Incident;
import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.IncidentRepository;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import com.isamm.gestion_incidents.Services.FileStorageService;
import com.isamm.gestion_incidents.Services.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final FileStorageService fileStorageService;

    private final UserRepository userRepository;
    @Override
    public void declarerIncident(
            IncidentRequest request,
            MultipartFile[] photos,
            String emailCitoyen
    ) { User citoyen = userRepository.findByEmail(emailCitoyen)
            .orElseThrow(() -> new RuntimeException("Citoyen introuvable"));
        List<String> fichiers = fileStorageService.saveFiles(photos);

        Incident incident = new Incident();
        incident.setTitre(request.getTitre());
        incident.setDescription(request.getDescription());
        incident.setCategory(request.getCategory());
        incident.setAdresse(request.getAdresse());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());
        incident.setPhotos(fichiers);
        incident.setCitoyen(citoyen);

        incidentRepository.save(incident);
    }
    @Override
    public List<Incident> getIncidentsByCitoyen(String email) {

        User citoyen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return incidentRepository.findByCitoyen(citoyen);
    }
    @Override
    public Incident getIncidentForEdit(Long id, String email) {
        User citoyen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Incident incident = incidentRepository.findByIdAndCitoyen(id, citoyen)
                .orElseThrow(() -> new RuntimeException("Incident introuvable"));

        if (incident.getStatut() != IncidentStatus.SIGNALE) {
            throw new RuntimeException("Incident non modifiable");
        }

        return incident;
    }

    @Override
    public void updateIncident(
            Long id,
            IncidentRequest request,
            MultipartFile[] newPhotos,
            String emailCitoyen
    ) {
        Incident incident = getIncidentForEdit(id, emailCitoyen);

        incident.setTitre(request.getTitre());
        incident.setDescription(request.getDescription());
        incident.setCategorie(request.getCategory());
        incident.setAdresse(request.getAdresse());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());

        if (newPhotos != null && newPhotos.length > 0) {
            List<String> fichiers = fileStorageService.saveFiles(newPhotos);
            incident.getPhotos().addAll(fichiers);
        }

        incidentRepository.save(incident);
    }

    @Override
    public void deleteIncident(Long id, String emailCitoyen) {
        Incident incident = getIncidentForEdit(id, emailCitoyen);
        incidentRepository.delete(incident);
    }

}
