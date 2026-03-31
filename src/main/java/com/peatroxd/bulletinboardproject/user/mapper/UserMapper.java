package com.peatroxd.bulletinboardproject.user.mapper;

import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "advertisements", ignore = true)
    void updateCurrentUser(UserUpdateRequest request, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "advertisements", ignore = true)
    void updateAdminUser(AdminUserUpdateRequest request, @MappingTarget User user);
}
