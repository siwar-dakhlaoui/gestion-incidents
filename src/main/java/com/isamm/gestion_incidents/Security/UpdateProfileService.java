package com.isamm.gestion_incidents.Security;

import com.isamm.gestion_incidents.DTO.request.UpdateProfileRequest;
import com.isamm.gestion_incidents.Models.User;

public interface UpdateProfileService {

    User updateUserProfile(String userEmail, UpdateProfileRequest request);

    User updatePassword(String userEmail, String currentPassword, String newPassword);

    boolean isEmailAvailable(String email, Long currentUserId);
}