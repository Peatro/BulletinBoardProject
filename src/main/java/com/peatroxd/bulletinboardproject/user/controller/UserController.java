package com.peatroxd.bulletinboardproject.user.controller;

import com.peatroxd.bulletinboardproject.common.exception.ApiErrorResponse;
import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Users", description = "Пользовательский и административный API")
public interface UserController {

    @Operation(summary = "Получить профиль текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль текущего пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    UserResponse getCurrentUser(@Parameter(hidden = true) @CurrentUser UUID keycloakUserId);

    @Operation(summary = "Обновить профиль текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    UserResponse updateCurrentUser(@Parameter(hidden = true) @CurrentUser UUID keycloakUserId, @Valid @RequestBody UserUpdateRequest request);

    @Operation(summary = "Получить пользователя по id", description = "Доступно только администратору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    UserResponse getUserById(@PathVariable UUID id);

    @Operation(summary = "Обновить пользователя", description = "Доступно только администратору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody AdminUserUpdateRequest request);

    @Operation(summary = "Удалить свой профиль")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Профиль удалён"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    void deleteCurrentUser(@Parameter(hidden = true) @CurrentUser UUID keycloakUserId);

    @Operation(summary = "Удалить пользователя", description = "Доступно только администратору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    void deleteUser(@PathVariable UUID id);
}
