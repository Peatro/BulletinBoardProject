package com.peatroxd.bulletinboardproject.user.controller;

import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.user.entity.User;

import java.util.UUID;

public interface UserController {

    User getUserById(UUID id);

    User createUser(AuthRegisterRequest request, Role role);

    User updateUser(UUID id, User user, Role role);

    void deleteUser(UUID id);
}
