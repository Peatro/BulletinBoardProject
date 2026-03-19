package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.security.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User create(User user, Role role);

    List<User> list();

    User findUserByIdOrThrow(UUID id);

    User update(UUID id, User user, Role role);

    void delete(UUID id);

    User findByUsernameOrThrow(String username);
}
