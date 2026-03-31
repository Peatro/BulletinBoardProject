package com.peatroxd.bulletinboardproject.advertisement.service;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;

import java.util.List;
import java.util.UUID;

public interface AdvertisementService {

    AdvertisementResponse createAdvertisement(AdvertisementCreateRequest request, UUID userId);

    List<AdvertisementResponse> getAllAdvertisements(Long categoryId, AdvertisementStatus status, UUID authorId);

    List<AdvertisementResponse> getAllAdvertisementsByUserId(UUID userId);

    AdvertisementResponse getAdvertisementById(Long id);

    AdvertisementResponse updateAdvertisement(Long id, AdvertisementCreateRequest request, UUID userId);

    void deleteAdvertisement(Long id, UUID userId);

    AdvertisementResponse publishAdvertisement(Long id, UUID userId);
}
