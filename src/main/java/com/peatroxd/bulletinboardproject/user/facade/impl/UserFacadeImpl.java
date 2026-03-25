package com.peatroxd.bulletinboardproject.user.facade.impl;

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
}
