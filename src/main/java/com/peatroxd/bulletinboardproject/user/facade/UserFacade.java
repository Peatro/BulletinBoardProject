package com.peatroxd.bulletinboardproject.user.facade;

import com.peatroxd.bulletinboardproject.user.entity.User;

import java.util.UUID;

public interface UserFacade {

    User getById(UUID id);

    boolean existsById(UUID id);
}
