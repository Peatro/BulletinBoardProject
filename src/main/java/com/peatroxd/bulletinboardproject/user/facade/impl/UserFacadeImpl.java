package com.peatroxd.bulletinboardproject.user.facade.impl;

import com.peatroxd.bulletinboardproject.user.dto.command.UserCreateCommand;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;



    @Override
    public User getById(UUID userId) {
        return userService.getUser(userId);
    }

    @Override
    public User getByKeycloakId(UUID keycloakUserId) {
        return userService.findByKeycloakUserIdOrThrow(keycloakUserId);
    }

    @Override
    public User createUser(UserCreateCommand command) {
        User user = User.builder()
                .keycloakUserId(command.keycloakUserId())
                .username(command.username())
                .email(command.email())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .phone(command.phone())
                .role(command.role())
                .enabled(command.enabled())
                .build();
        return userService.createLocalUser(user);
    }
}
