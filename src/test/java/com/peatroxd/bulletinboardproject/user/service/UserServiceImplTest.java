package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAdminClient;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.mapper.UserMapper;
import com.peatroxd.bulletinboardproject.user.repository.UserRepository;
import com.peatroxd.bulletinboardproject.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KeycloakAdminClient keycloakAdminClient;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getCurrentUserShouldMapUserEntityToResponse() {
        UUID keycloakUserId = UUID.randomUUID();
        User user = user(keycloakUserId);
        UserResponse expected = userResponse(keycloakUserId);

        when(userRepository.findByKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse response = userService.getCurrentUser(keycloakUserId);

        assertThat(response).isEqualTo(expected);
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.keycloakUserId()).isEqualTo(keycloakUserId);
    }

    @Test
    void updateCurrentUserShouldPersistChangedFields() {
        UUID keycloakUserId = UUID.randomUUID();
        User user = user(keycloakUserId);
        UserUpdateRequest request = new UserUpdateRequest(
                "updated@example.com",
                "Alice",
                "Johnson",
                "+79990000000"
        );
        UserResponse expected = new UserResponse(
                user.getId(), keycloakUserId, "alice",
                "updated@example.com", "Alice", "Johnson", "+79990000000", "USER", true
        );

        when(userRepository.findByKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        doAnswer(inv -> {
            UserUpdateRequest req = inv.getArgument(0);
            User u = inv.getArgument(1);
            u.setEmail(req.email());
            u.setFirstName(req.firstName());
            u.setLastName(req.lastName());
            u.setPhone(req.phone());
            return null;
        }).when(userMapper).updateCurrentUser(request, user);
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse response = userService.updateCurrentUser(keycloakUserId, request);

        assertThat(response.email()).isEqualTo("updated@example.com");
        assertThat(response.lastName()).isEqualTo("Johnson");
        assertThat(response.phone()).isEqualTo("+79990000000");
        InOrder inOrder = inOrder(userRepository, keycloakAdminClient);
        inOrder.verify(userRepository).saveAndFlush(user);
        inOrder.verify(keycloakAdminClient).updateCurrentUser(keycloakUserId, request);
    }

    @Test
    void getUserShouldReturnDto() {
        UUID userId = UUID.randomUUID();
        User user = user(UUID.randomUUID());
        user.setId(userId);
        UserResponse expected = new UserResponse(
                userId, user.getKeycloakUserId(), "alice",
                "alice@example.com", "Alice", "Smith", "+70000000000", "USER", true
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse response = userService.getUser(userId);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void updateUserShouldPersistAdminChanges() {
        UUID userId = UUID.randomUUID();
        User user = user(UUID.randomUUID());
        user.setId(userId);
        AdminUserUpdateRequest request = new AdminUserUpdateRequest(
                "moderator",
                "moderator@example.com",
                "Mila",
                "Brown",
                "+79991112233",
                Role.ADMIN,
                false
        );
        UserResponse expected = new UserResponse(
                userId, user.getKeycloakUserId(), "moderator",
                "moderator@example.com", "Mila", "Brown", "+79991112233", "ADMIN", false
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        doAnswer(inv -> {
            AdminUserUpdateRequest req = inv.getArgument(0);
            User u = inv.getArgument(1);
            u.setUsername(req.username());
            u.setEmail(req.email());
            u.setFirstName(req.firstName());
            u.setLastName(req.lastName());
            u.setPhone(req.phone());
            u.setRole(req.role());
            u.setEnabled(req.enabled());
            return null;
        }).when(userMapper).updateAdminUser(request, user);
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse response = userService.updateUser(userId, request);

        assertThat(response.username()).isEqualTo("moderator");
        assertThat(response.role()).isEqualTo("ADMIN");
        assertThat(response.enabled()).isFalse();
        InOrder inOrder = inOrder(userRepository, keycloakAdminClient);
        inOrder.verify(userRepository).saveAndFlush(user);
        inOrder.verify(keycloakAdminClient).updateUser(user.getKeycloakUserId(), request);
    }

    @Test
    void deleteCurrentUserShouldDeleteKeycloakAndLocalUser() {
        UUID keycloakUserId = UUID.randomUUID();
        User user = user(keycloakUserId);

        when(userRepository.findByKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(user));

        userService.deleteCurrentUser(keycloakUserId);

        InOrder inOrder = inOrder(userRepository, keycloakAdminClient);
        inOrder.verify(userRepository).delete(user);
        inOrder.verify(userRepository).flush();
        inOrder.verify(keycloakAdminClient).deleteUser(keycloakUserId);
    }

    @Test
    void deleteUserShouldDeleteKeycloakAndLocalUser() {
        UUID userId = UUID.randomUUID();
        UUID keycloakUserId = UUID.randomUUID();
        User user = user(keycloakUserId);
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        InOrder inOrder = inOrder(userRepository, keycloakAdminClient);
        inOrder.verify(userRepository).delete(user);
        inOrder.verify(userRepository).flush();
        inOrder.verify(keycloakAdminClient).deleteUser(keycloakUserId);
    }

    private User user(UUID keycloakUserId) {
        return User.builder()
                .id(UUID.randomUUID())
                .keycloakUserId(keycloakUserId)
                .username("alice")
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .phone("+70000000000")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    private UserResponse userResponse(UUID keycloakUserId) {
        return new UserResponse(
                UUID.randomUUID(), keycloakUserId, "alice",
                "alice@example.com", "Alice", "Smith", "+70000000000", "USER", true
        );
    }
}
