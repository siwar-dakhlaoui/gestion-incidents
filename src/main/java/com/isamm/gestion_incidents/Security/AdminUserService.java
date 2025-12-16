package com.isamm.gestion_incidents.Security;

import com.isamm.gestion_incidents.DTO.request.AdminCreateUserRequest;

public interface AdminUserService {
    void createUserByAdmin(AdminCreateUserRequest request);
}
