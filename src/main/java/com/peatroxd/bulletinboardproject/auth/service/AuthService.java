package com.peatroxd.bulletinboardproject.auth.service;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthRegisterResponse;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;

public interface AuthService {

    AuthRegisterResponse register(AuthRegisterRequest request);

    AuthTokenResponse login(AuthLoginRequest request);
}
