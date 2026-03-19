package com.peatroxd.bulletinboardproject.auth.service;

import com.peatroxd.bulletinboardproject.user.dto.request.UserCreateRequest;

public interface AuthService {

    void register(UserCreateRequest request);
}
