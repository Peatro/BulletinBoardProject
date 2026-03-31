package com.peatroxd.bulletinboardproject.user.service.impl;

import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
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

    public User createLocalUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(UUID id) {
        return findUserByIdOrThrow(id);
    }

    public UserResponse getCurrentUser(UUID keycloakUserId) {
        return UserResponse.from(findByKeycloakUserIdOrThrow(keycloakUserId));
    }

    public UserResponse updateCurrentUser(UUID keycloakUserId, UserUpdateRequest request) {
        User existing = findByKeycloakUserIdOrThrow(keycloakUserId);
        existing.setEmail(request.email());
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setPhone(request.phone());
        return UserResponse.from(userRepository.save(existing));
    }

    public User updateUser(UUID id, User user, Role role) {
        User existing = findUserByIdOrThrow(id);
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
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
