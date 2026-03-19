package com.peatroxd.bulletinboardproject.auth.service.impl;

import com.peatroxd.bulletinboardproject.auth.service.AuthService;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.user.dto.request.UserCreateRequest;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    @Override
    public void register(UserCreateRequest request) {
        userService.createUser(request, Role.USER);
    }
}
