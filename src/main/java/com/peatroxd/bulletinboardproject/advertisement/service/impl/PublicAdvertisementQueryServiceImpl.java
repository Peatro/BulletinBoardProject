package com.peatroxd.bulletinboardproject.advertisement.service.impl;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.PublicAdvertisementFilter;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.mapper.AdvertisementMapper;
import com.peatroxd.bulletinboardproject.advertisement.repository.AdvertisementRepository;
import com.peatroxd.bulletinboardproject.advertisement.service.PublicAdvertisementQueryService;
import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.BadRequestException;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicAdvertisementQueryServiceImpl implements PublicAdvertisementQueryService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper advertisementMapper;

    @Override
    public List<AdvertisementResponse> getAllAdvertisements(PublicAdvertisementFilter filter) {
        AdvertisementStatus effectiveStatus = filter.status() == null ? AdvertisementStatus.PUBLISHED : filter.status();
        validatePublicStatusFilter(effectiveStatus);

        return advertisementRepository.findAllByPublicFilters(effectiveStatus, filter.categoryId(), filter.authorId())
                .stream()
                .map(advertisementMapper::toResponse)
                .toList();
    }

    @Override
    public AdvertisementResponse getAdvertisementById(Long id) {
        Advertisement advertisement = advertisementRepository.findByIdAndStatus(id, AdvertisementStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException(
                        NotFoundExceptionMessage.ADVERTISEMENT_NOT_FOUND.getMessage()
                ));
        return advertisementMapper.toResponse(advertisement);
    }

    private void validatePublicStatusFilter(AdvertisementStatus status) {
        if (status != AdvertisementStatus.PUBLISHED) {
            throw new BadRequestException("Public list supports only PUBLISHED status");
        }
    }
}
