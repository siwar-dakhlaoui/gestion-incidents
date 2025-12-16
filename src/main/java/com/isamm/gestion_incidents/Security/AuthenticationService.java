package com.isamm.gestion_incidents.Security;

import com.isamm.gestion_incidents.DTO.request.AdminCreateUserRequest;
import com.isamm.gestion_incidents.DTO.request.SignInRequest;
import com.isamm.gestion_incidents.DTO.request.SignUpRequest;
import com.isamm.gestion_incidents.DTO.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse SignUp(SignUpRequest request);
    JwtAuthenticationResponse SignIn(SignInRequest request);
}
