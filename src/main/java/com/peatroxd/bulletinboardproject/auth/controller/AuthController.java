package com.peatroxd.bulletinboardproject.auth.controller;

import com.peatroxd.bulletinboardproject.auth.service.AuthService;
import com.peatroxd.bulletinboardproject.user.dto.request.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserCreateRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).build();
    }
}
