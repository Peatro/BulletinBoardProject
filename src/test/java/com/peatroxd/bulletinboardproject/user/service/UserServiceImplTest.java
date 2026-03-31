package com.peatroxd.bulletinboardproject.user.service;

import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.repository.UserRepository;
import com.peatroxd.bulletinboardproject.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getCurrentUserShouldMapUserEntityToResponse() {
        UUID keycloakUserId = UUID.randomUUID();
        User user = User.builder()
                .id(UUID.randomUUID())
                .keycloakUserId(keycloakUserId)
                .username("alice")
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .phone("+70000000000")
                .enabled(true)
                .build();

        when(userRepository.findByKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUser(keycloakUserId);

        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.firstName()).isEqualTo("Alice");
        assertThat(response.lastName()).isEqualTo("Smith");
        assertThat(response.phone()).isEqualTo("+70000000000");
        assertThat(response.keycloakUserId()).isEqualTo(keycloakUserId);
    }
}
