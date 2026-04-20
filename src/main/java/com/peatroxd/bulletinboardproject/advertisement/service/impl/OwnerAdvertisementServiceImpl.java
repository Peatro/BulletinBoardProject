package com.peatroxd.bulletinboardproject.advertisement.service.impl;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.mapper.AdvertisementMapper;
import com.peatroxd.bulletinboardproject.advertisement.repository.AdvertisementRepository;
import com.peatroxd.bulletinboardproject.advertisement.service.OwnerAdvertisementService;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.facade.CategoryFacade;
import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.BadRequestException;
import com.peatroxd.bulletinboardproject.common.exception.ForbiddenOperationException;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerAdvertisementServiceImpl implements OwnerAdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryFacade categoryFacade;
    private final UserFacade userFacade;
    private final AdvertisementMapper advertisementMapper;

    @Override
    @Transactional
    public AdvertisementResponse createAdvertisement(AdvertisementCreateRequest request, UUID userId) {
        User author = userFacade.getByKeycloakId(userId);
        Category category = categoryFacade.getById(request.categoryId());

        Advertisement advertisement = advertisementMapper.toEntity(request);
        advertisement.setAuthor(author);
        advertisement.setCategory(category);
        advertisement.setStatus(AdvertisementStatus.DRAFT);

        return advertisementMapper.toResponse(advertisementRepository.save(advertisement));
    }

    @Override
    public List<AdvertisementResponse> getAllAdvertisementsByUserId(UUID userId) {
        return advertisementRepository.findAllByAuthor_KeycloakUserId(userId)
                .stream()
                .map(advertisementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AdvertisementResponse updateAdvertisement(Long id, AdvertisementCreateRequest request, UUID userId) {
        Advertisement advertisement = findByIdOrThrow(id);
        validateOwnership(advertisement, userId);

        advertisementMapper.updateEntity(request, advertisement);
        advertisement.setCategory(categoryFacade.getById(request.categoryId()));

        return advertisementMapper.toResponse(advertisementRepository.save(advertisement));
    }

    @Override
    @Transactional
    public void deleteAdvertisement(Long id, UUID userId) {
        Advertisement advertisement = findByIdOrThrow(id);
        validateOwnership(advertisement, userId);
        advertisementRepository.delete(advertisement);
    }

    @Override
    @Transactional
    public AdvertisementResponse publishAdvertisement(Long id, UUID userId) {
        Advertisement advertisement = findByIdOrThrow(id);
        validateOwnership(advertisement, userId);
        validatePublishAllowed(advertisement);

        advertisement.setStatus(AdvertisementStatus.PUBLISHED);
        advertisement.setPublishedAt(LocalDateTime.now());

        return advertisementMapper.toResponse(advertisementRepository.save(advertisement));
    }

    private Advertisement findByIdOrThrow(Long id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        NotFoundExceptionMessage.ADVERTISEMENT_NOT_FOUND.getMessage()
                ));
    }

    private void validateOwnership(Advertisement advertisement, UUID userId) {
        if (!advertisement.getAuthor().getKeycloakUserId().equals(userId)) {
            throw new ForbiddenOperationException("Forbidden");
        }
    }

    private void validatePublishAllowed(Advertisement advertisement) {
        if (advertisement.getStatus() != AdvertisementStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT can be published");
        }
    }
}
