package com.peatroxd.bulletinboardproject.advertisement.dto.response;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AdvertisementResponse(
        Long id,
        String title,
        String description,

        BigDecimal price,
        String currency,

        AdvertisementStatus status,
        UUID authorId,

        LocalDateTime createdAt
) {
}
