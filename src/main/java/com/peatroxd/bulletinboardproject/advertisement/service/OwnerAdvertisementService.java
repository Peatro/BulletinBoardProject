package com.peatroxd.bulletinboardproject.advertisement.service;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;

import java.util.List;
import java.util.UUID;

public interface OwnerAdvertisementService {

    AdvertisementResponse createAdvertisement(AdvertisementCreateRequest request, UUID userId);

    List<AdvertisementResponse> getAllAdvertisementsByUserId(UUID userId);

    AdvertisementResponse updateAdvertisement(Long id, AdvertisementCreateRequest request, UUID userId);

    void deleteAdvertisement(Long id, UUID userId);

    AdvertisementResponse publishAdvertisement(Long id, UUID userId);
}
