package com.peatroxd.bulletinboardproject.advertisement.service;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.CreateAdvertisementRequest;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvertisementService {

    Advertisement create(CreateAdvertisementRequest request, UUID userId);

    List<Advertisement> list();

    Optional<Advertisement> get(Long id);

    Advertisement update(Advertisement a);

    void delete(Long id);
}
