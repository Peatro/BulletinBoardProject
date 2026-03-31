package com.peatroxd.bulletinboardproject.advertisement.service;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.PublicAdvertisementFilter;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;

import java.util.List;

public interface PublicAdvertisementQueryService {

    List<AdvertisementResponse> getAllAdvertisements(PublicAdvertisementFilter filter);

    AdvertisementResponse getAdvertisementById(Long id);
}
