package com.peatroxd.bulletinboardproject.user.controller;

import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.security.Role;

import java.util.UUID;

public interface UserController {

    User getUserById(UUID id);

    User createUser(User user, Role role);

    User updateUser(UUID id, User user, Role role);

    void deleteUser(UUID id);
}
