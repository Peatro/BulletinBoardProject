package com.peatroxd.bulletinboardproject.user.controller.impl;

import com.peatroxd.bulletinboardproject.common.exception.ApiErrorResponse;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import com.peatroxd.bulletinboardproject.user.controller.UserController;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Пользовательский и административный API")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль текущего пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public UserResponse getCurrentUser(@CurrentUser UUID keycloakUserId) {
        return userService.getCurrentUser(keycloakUserId);
    }

    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public UserResponse updateCurrentUser(
            @CurrentUser UUID keycloakUserId,
            @RequestBody UserUpdateRequest request
    ) {
        return userService.updateCurrentUser(keycloakUserId, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить пользователя по id", description = "Доступно только администратору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public User getUserById(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить пользователя", description = "Доступно только администратору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public User updateUser(@PathVariable UUID id, @RequestBody User user, @RequestParam Role role) {
        return userService.updateUser(id, user, role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пользователя", description = "Доступно только администратору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
