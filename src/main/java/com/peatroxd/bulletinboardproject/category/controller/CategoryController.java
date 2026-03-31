package com.peatroxd.bulletinboardproject.category.controller;

import com.peatroxd.bulletinboardproject.category.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Categories", description = "Справочник категорий объявлений")
public interface CategoryController {

    @GetMapping
    @Operation(summary = "Получить список категорий")
    @ApiResponse(
            responseCode = "200",
            description = "Список категорий",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)))
    )
    ResponseEntity<List<CategoryResponse>> getAllCategories();
}
