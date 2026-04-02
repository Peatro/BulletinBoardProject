package com.peatroxd.bulletinboardproject.advertisement.dto.request;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;

import java.util.UUID;

public record PublicAdvertisementFilter(
        Long categoryId,
        AdvertisementStatus status,
        UUID authorId
) {
}
