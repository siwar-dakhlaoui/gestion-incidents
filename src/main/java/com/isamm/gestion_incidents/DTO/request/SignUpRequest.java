package com.isamm.gestion_incidents.DTO.request;

import com.isamm.gestion_incidents.Models.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.CITOYEN;


}
