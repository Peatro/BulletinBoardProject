package com.peatroxd.bulletinboardproject.advertisement.controller;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.common.exception.ApiErrorResponse;
import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Advertisements", description = "Управление объявлениями")
public interface AdvertisementController {

    @PostMapping
    @Operation(summary = "Создать объявление", description = "Создает новое объявление от имени текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Объявление создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<AdvertisementResponse> createAdvertisement(
            @RequestBody AdvertisementCreateRequest request,
            @Parameter(hidden = true)
            @CurrentUser UUID userId
    );

    @GetMapping("/{id}")
    @Operation(summary = "Получить объявление по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление найдено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<AdvertisementResponse> getAdvertisementById(
            @PathVariable Long id
    );

    @GetMapping
    @Operation(summary = "Получить список объявлений")
    @ApiResponse(
            responseCode = "200",
            description = "Список объявлений",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdvertisementResponse.class)))
    )
    ResponseEntity<List<AdvertisementResponse>> getAllAdvertisements();

    @GetMapping("/me")
    @Operation(summary = "Получить объявления текущего пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список объявлений текущего пользователя",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdvertisementResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<List<AdvertisementResponse>> getCurrentUserAdvertisements(
            @Parameter(hidden = true)
            @CurrentUser UUID userId
    );

    @PutMapping("/{id}")
    @Operation(summary = "Обновить объявление", description = "Обновляет объявление, если оно принадлежит текущему пользователю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<AdvertisementResponse> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody AdvertisementCreateRequest request,
            @Parameter(hidden = true)
            @CurrentUser UUID userId
    );

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить объявление", description = "Удаляет объявление, если оно принадлежит текущему пользователю")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Объявление удалено"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<Void> deleteAdvertisement(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @CurrentUser UUID userId
    );

    @PatchMapping("/{id}")
    @Operation(summary = "Опубликовать объявление", description = "Переводит объявление из DRAFT в PUBLISHED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление опубликовано"),
            @ApiResponse(responseCode = "400", description = "Объявление нельзя опубликовать в текущем статусе", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Нет прав на публикацию", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<AdvertisementResponse> publish(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @CurrentUser UUID userId
    );
}
