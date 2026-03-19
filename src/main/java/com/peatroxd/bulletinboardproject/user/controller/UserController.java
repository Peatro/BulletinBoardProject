package com.peatroxd.bulletinboardproject.user.controller;

import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.security.Role;

import java.util.List;
import java.util.UUID;

public interface UserController {

    List<User> list();

    User get(UUID id);

    User create(User user, Role role);

    User update(UUID id, User user, Role role);

    void delete(UUID id);
}
