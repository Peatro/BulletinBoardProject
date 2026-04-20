package com.peatroxd.bulletinboardproject.user.controller.impl;

import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import com.peatroxd.bulletinboardproject.user.controller.UserController;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(@CurrentUser UUID keycloakUserId) {
        return userService.getCurrentUser(keycloakUserId);
    }

    @PutMapping("/me")
    public UserResponse updateCurrentUser(@CurrentUser UUID keycloakUserId, @RequestBody UserUpdateRequest request) {
        return userService.updateCurrentUser(keycloakUserId, request);
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser(@CurrentUser UUID keycloakUserId) {
        userService.deleteCurrentUser(keycloakUserId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable UUID id, @RequestBody AdminUserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
