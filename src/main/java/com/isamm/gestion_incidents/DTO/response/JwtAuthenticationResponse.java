package com.isamm.gestion_incidents.DTO.response;

import com.isamm.gestion_incidents.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private Long userId;
    private String role;
    private boolean isVerified;

}
