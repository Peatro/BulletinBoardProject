package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.entity.User;

import java.util.UUID;

public interface UserService {

    User createLocalUser(User user);

    UserResponse getUser(UUID id);

    UserResponse getCurrentUser(UUID keycloakUserId);

    UserResponse updateCurrentUser(UUID keycloakUserId, UserUpdateRequest request);

    UserResponse updateUser(UUID id, AdminUserUpdateRequest request);

    void deleteUser(UUID id);

    User findByUsernameOrThrow(String username);

    User findByIdOrThrow(UUID id);

    User findByKeycloakUserIdOrThrow(UUID keycloakUserId);
}
