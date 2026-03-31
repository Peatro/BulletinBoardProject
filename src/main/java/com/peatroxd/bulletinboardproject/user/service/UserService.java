package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.entity.User;

import java.util.UUID;

public interface UserService {

    User createLocalUser(User user);

    User getUser(UUID id);

    UserResponse getCurrentUser(UUID keycloakUserId);

    UserResponse updateCurrentUser(UUID keycloakUserId, UserUpdateRequest request);

    User updateUser(UUID id, User user, Role role);

    void deleteUser(UUID id);

    User findByUsernameOrThrow(String username);

    User findByKeycloakUserIdOrThrow(UUID keycloakUserId);
}
