package com.peatroxd.bulletinboardproject.user.facade.impl;

import com.peatroxd.bulletinboardproject.user.dto.command.UserCreateCommand;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import com.peatroxd.bulletinboardproject.user.mapper.UserMapper;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public User getById(UUID userId) {
        return userService.findByIdOrThrow(userId);
    }

    @Override
    public User getByKeycloakId(UUID keycloakUserId) {
        return userService.findByKeycloakUserIdOrThrow(keycloakUserId);
    }

    @Override
    public User createUser(UserCreateCommand command) {
        return userService.createLocalUser(userMapper.toEntity(command));
    }
}
