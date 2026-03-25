package com.peatroxd.bulletinboardproject.user.service.impl;

import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAdminClient;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.repository.UserRepository;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminClient keycloakAdminClient;

    public User createUser(AuthRegisterRequest request, Role role) {
        UUID keycloakUserId = keycloakAdminClient.createUser(request, role);
        User user = User.builder()
                .keycloakUserId(keycloakUserId)
                .username(request.username())
                .email(request.email())
                .name(request.firstName())
                .phone(request.phone())
                .role(role)
                .enabled(true)
                .build();
        try {
            return userRepository.save(user);
        } catch (RuntimeException ex) {
            try {
                keycloakAdminClient.deleteUser(keycloakUserId);
            } catch (RuntimeException ignored) {
            }
            throw ex;
        }
    }

    public User getUser(UUID id) {
        return findUserByIdOrThrow(id);
    }

    public User updateUser(UUID id, User user, Role role) {
        User existing = findUserByIdOrThrow(id);
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setName(user.getName());
        existing.setPhone(user.getPhone());
        existing.setRole(role);
        existing.setEnabled(user.isEnabled());
        return userRepository.save(existing);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public User findByUsernameOrThrow(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.USER_NOT_FOUND.getMessage()));
    }

    public User findByKeycloakUserIdOrThrow(UUID keycloakUserId) {
        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.USER_NOT_FOUND.getMessage()));
    }

    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.USER_NOT_FOUND.getMessage()));
    }
}
