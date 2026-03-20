package com.peatroxd.bulletinboardproject.advertisement.mapper;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper {

    Advertisement toAdvertisementEntity(AdvertisementCreateRequest advertisement);

    AdvertisementResponse toAdvertisementResponseDto(Advertisement advertisement);
}
