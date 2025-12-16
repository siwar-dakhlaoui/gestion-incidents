package com.isamm.gestion_incidents.Repositories;

import com.isamm.gestion_incidents.Enum.IncidentCategory;
import com.isamm.gestion_incidents.Enum.IncidentStatus;
import com.isamm.gestion_incidents.Models.Incident;
import com.isamm.gestion_incidents.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // Trouver les incidents d'un citoyen
    Page<Incident> findByCitoyen(User citoyen, Pageable pageable);

    // Trouver les incidents assignés à un agent
    Page<Incident> findByAgentAssigne(User agent, Pageable pageable);

    // Trouver les incidents par statut
    Page<Incident> findByStatut(IncidentStatus statut, Pageable pageable);

    // Trouver les incidents par catégorie
    Page<Incident> findByCategorie(IncidentCategory categorie, Pageable pageable);

    // Trouver les incidents par quartier
    Page<Incident> findByQuartier(String quartier, Pageable pageable);

    // Recherche avancée avec filtres multiples
    @Query("SELECT i FROM Incident i WHERE " +
            "(:categorie IS NULL OR i.categorie = :categorie) AND " +
            "(:statut IS NULL OR i.statut = :statut) AND " +
            "(:quartier IS NULL OR i.quartier = :quartier) AND " +
            "(:dateDebut IS NULL OR i.dateDeclaration >= :dateDebut) AND " +
            "(:dateFin IS NULL OR i.dateDeclaration <= :dateFin)")
    Page<Incident> searchIncidents(
            @Param("categorie") IncidentCategory categorie,
            @Param("statut") IncidentStatus statut,
            @Param("quartier") String quartier,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable);

    // Compter les incidents par catégorie (pour statistiques)
    @Query("SELECT i.categorie, COUNT(i) FROM Incident i GROUP BY i.categorie")
    List<Object[]> countByCategorie();
}