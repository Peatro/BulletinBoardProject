package com.peatroxd.bulletinboardproject.user.controller;

import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;

import java.util.UUID;

public interface UserController {

    UserResponse getCurrentUser(@CurrentUser UUID keycloakUserId);

    UserResponse updateCurrentUser(@CurrentUser UUID keycloakUserId, UserUpdateRequest request);

    UserResponse getUserById(UUID id);

    UserResponse updateUser(UUID id, AdminUserUpdateRequest request);

    void deleteUser(UUID id);
}
