package com.peatroxd.bulletinboardproject.user.repository;

import com.peatroxd.bulletinboardproject.AbstractPostgresContainerTest;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest extends AbstractPostgresContainerTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void findByKeycloakUserIdShouldReturnMatchingUser() {
        UUID keycloakUserId = UUID.randomUUID();
        userRepository.save(User.builder()
                .username("alice")
                .email("alice@example.com")
                .firstName("Alice")
                .role(Role.USER)
                .enabled(true)
                .keycloakUserId(keycloakUserId)
                .build());

        assertThat(userRepository.findByKeycloakUserId(keycloakUserId))
                .get()
                .extracting(User::getUsername, User::getKeycloakUserId)
                .containsExactly("alice", keycloakUserId);
    }

    @Test
    void findUserByUsernameShouldReturnMatchingUser() {
        UUID keycloakUserId = UUID.randomUUID();
        userRepository.save(User.builder()
                .username("bob")
                .email("bob@example.com")
                .firstName("Bob")
                .role(Role.USER)
                .enabled(true)
                .keycloakUserId(keycloakUserId)
                .build());

        assertThat(userRepository.findUserByUsername("bob"))
                .get()
                .extracting(User::getUsername, User::getKeycloakUserId)
                .containsExactly("bob", keycloakUserId);
    }
}
