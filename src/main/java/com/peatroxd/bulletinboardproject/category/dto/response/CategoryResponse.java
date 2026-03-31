package com.peatroxd.bulletinboardproject.category.dto.response;

import com.peatroxd.bulletinboardproject.category.enitty.Category;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Категория объявления")
public record CategoryResponse(
        @Schema(description = "Идентификатор категории", example = "2")
        Long id,
        @Schema(description = "Название категории", example = "Cars")
        String name,
        @Schema(description = "Идентификатор родительской категории", example = "1", nullable = true)
        Long parentId
) {
    public static CategoryResponse from(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryResponse(category.getId(), category.getName(), parentId);
    }
}
