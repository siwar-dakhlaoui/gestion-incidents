package com.isamm.gestion_incidents.DTO.request;

import com.isamm.gestion_incidents.Enum.IncidentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentRequest {

   @NotBlank
   private String titre;

   @NotBlank
   private String description;

   @NotNull
   private IncidentCategory category;

   private String adresse;

   private Double latitude;
   private Double longitude;
}
