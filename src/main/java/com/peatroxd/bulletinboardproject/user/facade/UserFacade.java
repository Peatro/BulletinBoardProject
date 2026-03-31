package com.peatroxd.bulletinboardproject.user.facade;

import com.peatroxd.bulletinboardproject.user.dto.command.UserCreateCommand;
import com.peatroxd.bulletinboardproject.user.entity.User;

import java.util.UUID;

public interface UserFacade {

    User getById(UUID id);

    User getByKeycloakId(UUID keycloakUserId);

    User createUser(UserCreateCommand command);
}
