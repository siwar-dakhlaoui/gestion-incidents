package com.isamm.gestion_incidents.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "quartiers")
public class Quartier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String description;

    @Column(name = "code_postal")
    private String codePostal;
}
