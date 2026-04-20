package com.peatroxd.bulletinboardproject.security.keycloak;

import com.peatroxd.bulletinboardproject.common.exception.ConflictException;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final Keycloak keycloak;
    private final KeycloakAdminProperties properties;

    public UUID createUser(
            String username,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password,
            Role role
    ) {
        UserRepresentation user = buildUserRepresentation(username, email, firstName, lastName, phone, password);

        RealmResource realm = keycloak.realm(properties.realm());
        try (Response response = realm.users().create(user)) {
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new ConflictException("User already exists with the same username or email");
            }
            UUID userId = UUID.fromString(CreatedResponseUtil.getCreatedId(response));
            assignRealmRole(realm, userId, role.name());
            return userId;
        }
    }

    public void deleteUser(UUID userId) {
        keycloak.realm(properties.realm()).users().delete(userId.toString());
    }

    public void updateCurrentUser(UUID keycloakUserId, UserUpdateRequest request) {
        RealmResource realm = keycloak.realm(properties.realm());
        UserResource userResource = realm.users().get(keycloakUserId.toString());
        UserRepresentation user = userResource.toRepresentation();

        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        if (StringUtils.hasText(request.lastName())) {
            user.setLastName(request.lastName());
        }
        if (StringUtils.hasText(request.phone())) {
            user.setAttributes(Map.of("phone", List.of(request.phone())));
        }
        userResource.update(user);
    }

    public void updateUser(UUID keycloakUserId, AdminUserUpdateRequest request) {
        RealmResource realm = keycloak.realm(properties.realm());
        UserResource userResource = realm.users().get(keycloakUserId.toString());
        UserRepresentation user = userResource.toRepresentation();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setEnabled(request.enabled());
        if (StringUtils.hasText(request.lastName())) {
            user.setLastName(request.lastName());
        }
        if (StringUtils.hasText(request.phone())) {
            user.setAttributes(Map.of("phone", List.of(request.phone())));
        }
        userResource.update(user);

        updateUserRole(realm, keycloakUserId, request.role());
    }

    private void updateUserRole(RealmResource realm, UUID userId, Role newRole) {
        var rolesResource = realm.users().get(userId.toString()).roles().realmLevel();

        List<RoleRepresentation> currentAppRoles = rolesResource.listEffective().stream()
                .filter(r -> Arrays.stream(Role.values()).anyMatch(appRole -> appRole.name().equals(r.getName())))
                .toList();
        if (!currentAppRoles.isEmpty()) {
            rolesResource.remove(currentAppRoles);
        }

        RoleRepresentation role = realm.roles().get(newRole.name()).toRepresentation();
        rolesResource.add(List.of(role));
    }

    private UserRepresentation buildUserRepresentation(
            String username, String email, String firstName,
            String lastName, String phone, String password
    ) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setEnabled(true);

        if (StringUtils.hasText(lastName)) {
            user.setLastName(lastName);
        }
        if (StringUtils.hasText(phone)) {
            user.setAttributes(Map.of("phone", List.of(phone)));
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        return user;
    }

    private void assignRealmRole(RealmResource realm, UUID userId, String roleName) {
        RoleRepresentation role = realm.roles().get(roleName).toRepresentation();
        realm.users().get(userId.toString()).roles().realmLevel().add(List.of(role));
    }
}
