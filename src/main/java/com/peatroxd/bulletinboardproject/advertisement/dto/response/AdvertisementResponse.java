package com.peatroxd.bulletinboardproject.advertisement.dto.response;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import com.peatroxd.bulletinboardproject.image.entity.Image;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record AdvertisementResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        AdvertisementStatus status,
        AdvertisementType type,
        UUID authorId,
        String authorName,
        String authorPhone,
        Long categoryId,
        String categoryName,
        List<String> imagesKeys,
        LocalDateTime createdAt
) {
}
