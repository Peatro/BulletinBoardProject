package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.user.entity.User;

import java.util.UUID;

public interface UserService {

    User createUser(AuthRegisterRequest request, Role role);

    User getUser(UUID id);

    User updateUser(UUID id, User user, Role role);

    void deleteUser(UUID id);

    User findByUsernameOrThrow(String username);

    User findByKeycloakUserIdOrThrow(UUID keycloakUserId);
}
