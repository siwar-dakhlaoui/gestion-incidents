package com.isamm.gestion_incidents.DTO.request;

import com.isamm.gestion_incidents.Enum.IncidentCategory;
import org.antlr.v4.runtime.misc.NotNull;

public class IncidentRequest {
 //   @NotBlank
    private String description;

    @NotNull
    private IncidentCategory category;

    @NotNull
    private Long quartierId;
}
