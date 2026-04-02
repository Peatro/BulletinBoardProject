package com.peatroxd.bulletinboardproject.advertisement.dto.response;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Представление объявления в API")
public record AdvertisementResponse(
        @Schema(description = "Идентификатор объявления", example = "42")
        Long id,
        @Schema(description = "Заголовок объявления", example = "Продам Toyota Camry")
        String title,
        @Schema(description = "Описание объявления", example = "Автомобиль в хорошем состоянии")
        String description,
        @Schema(description = "Цена", example = "1250000.00")
        BigDecimal price,
        @Schema(description = "Статус объявления", example = "PUBLISHED")
        AdvertisementStatus status,
        @Schema(description = "Тип объявления", example = "SELL")
        AdvertisementType type,
        @Schema(description = "Идентификатор автора", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID authorId,
        @Schema(description = "Имя автора", example = "alice")
        String authorName,
        @Schema(description = "Телефон автора", example = "+70000000000", nullable = true)
        String authorPhone,
        @Schema(description = "Идентификатор категории", example = "10")
        Long categoryId,
        @Schema(description = "Название категории", example = "Cars")
        String categoryName,
        @Schema(description = "Ключи изображений объявления")
        List<String> imagesKeys,
        @Schema(description = "Дата создания объявления", example = "2026-03-31T12:00:00")
        LocalDateTime createdAt
) {
}
