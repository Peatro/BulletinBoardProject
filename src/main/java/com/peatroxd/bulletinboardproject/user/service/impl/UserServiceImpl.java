package com.peatroxd.bulletinboardproject.user.service.impl;

import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAdminClient;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.mapper.UserMapper;
import com.peatroxd.bulletinboardproject.user.repository.UserRepository;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakAdminClient keycloakAdminClient;

    @Override
    public User createLocalUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserResponse getUser(UUID id) {
        return userMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public UserResponse getCurrentUser(UUID keycloakUserId) {
        return userMapper.toResponse(findByKeycloakUserIdOrThrow(keycloakUserId));
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UUID keycloakUserId, UserUpdateRequest request) {
        User existing = findByKeycloakUserIdOrThrow(keycloakUserId);
        userMapper.updateCurrentUser(request, existing);
        User savedUser = userRepository.saveAndFlush(existing);
        keycloakAdminClient.updateCurrentUser(keycloakUserId, request);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, AdminUserUpdateRequest request) {
        User existing = findByIdOrThrow(id);
        userMapper.updateAdminUser(request, existing);
        User savedUser = userRepository.saveAndFlush(existing);
        keycloakAdminClient.updateUser(existing.getKeycloakUserId(), request);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteCurrentUser(UUID keycloakUserId) {
        User existing = findByKeycloakUserIdOrThrow(keycloakUserId);
        userRepository.delete(existing);
        userRepository.flush();
        keycloakAdminClient.deleteUser(existing.getKeycloakUserId());
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User existing = findByIdOrThrow(id);
        userRepository.delete(existing);
        userRepository.flush();
        keycloakAdminClient.deleteUser(existing.getKeycloakUserId());
    }

    @Override
    public User findByUsernameOrThrow(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.USER_NOT_FOUND.getMessage()));
    }

    @Override
    public User findByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.USER_NOT_FOUND.getMessage()));
    }

    @Override
    public User findByKeycloakUserIdOrThrow(UUID keycloakUserId) {
        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.USER_NOT_FOUND.getMessage()));
    }
}
