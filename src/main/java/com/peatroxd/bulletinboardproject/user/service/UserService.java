package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.security.Role;

import java.util.UUID;

public interface UserService {

    User createUser(User user, Role role);

    User getUser(UUID id);

    User updateUser(UUID id, User user, Role role);

    void deleteUser(UUID id);

    User findByUsernameOrThrow(String username);
}
