package com.isamm.gestion_incidents.Models;

import com.isamm.gestion_incidents.Enum.IncidentCategory;
import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Enum.PriorityLevel;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "incidents")
@Data
@NoArgsConstructor

@AllArgsConstructor
@Builder
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private IncidentCategory categorie;

    @Enumerated(EnumType.STRING)
    private IncidentStatus statut;

    private String adresse;
    private Double latitude;
    private Double longitude;
    private String quartier;

    @ManyToOne
    @JoinColumn(name = "citoyen_id", nullable = false)
    private User citoyen;

    @ManyToOne
    @JoinColumn(name = "agent_assigne_id")
    private User agentAssigne;

    private LocalDateTime dateDeclaration;
    private LocalDateTime datePriseEnCharge;
    private LocalDateTime dateResolution;

   /* @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;*/

    @Column(name = "priorite")
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorite;

    @PrePersist
    protected void onCreate() {
        dateDeclaration = LocalDateTime.now();
        statut = IncidentStatus.SIGNALE;
        priorite = PriorityLevel.MOYENNE;
    }

    public IncidentStatus getStatus() {return statut;}

    public void setAgent(User agent) {
    }

    public void setStatus(IncidentStatus incidentStatus) {

    }

    public void setResolvedAt(LocalDateTime now) {

    }
}