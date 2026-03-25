package com.peatroxd.bulletinboardproject.auth.service;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;

public interface AuthService {

    void register(AuthRegisterRequest request);

    AuthTokenResponse login(AuthLoginRequest request);
}
