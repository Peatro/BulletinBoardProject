package com.peatroxd.bulletinboardproject.advertisement.service.impl;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.mapper.AdvertisementMapper;
import com.peatroxd.bulletinboardproject.advertisement.repository.AdvertisementRepository;
import com.peatroxd.bulletinboardproject.advertisement.service.AdvertisementService;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.facade.CategoryFacade;
import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryFacade categoryFacade;
    private final UserFacade userFacade;


    private final AdvertisementMapper mapper;

    @Override
    public Advertisement create(AdvertisementCreateRequest request, UUID userId) {

        User author = userFacade.getById(userId);
        Category category = categoryFacade.getById(request.categoryId());

        Advertisement advertisement = mapper.toAdvertisementEntity(request);

        advertisement.setAuthor(author);
        advertisement.setCategory(category);
        advertisement.setStatus(AdvertisementStatus.DRAFT);
        advertisement.setCreatedAt(LocalDateTime.now());
        advertisement.setUpdatedAt(LocalDateTime.now());

        return advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement publish(Long id, UUID userId) {

        Advertisement advertisement = findByIdOrThrow(id);

        if (!advertisement.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        if (advertisement.getStatus() != AdvertisementStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT can be published");
        }

        advertisement.setStatus(AdvertisementStatus.PUBLISHED);
        advertisement.setPublishedAt(LocalDateTime.now());
        advertisement.setUpdatedAt(LocalDateTime.now());

        return advertisementRepository.save(advertisement);
    }

    public List<Advertisement> list() {
        return advertisementRepository.findAll();
    }

    public Optional<Advertisement> get(Long id) {
        return advertisementRepository.findById(id);
    }

    public Advertisement update(Advertisement a) {
        a.setUpdatedAt(LocalDateTime.now());
        return advertisementRepository.save(a);
    }

    public void delete(Long id) {
        advertisementRepository.deleteById(id);
    }

    private Advertisement findByIdOrThrow(Long id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.ADVERTISEMENT_NOT_FOUND.getMessage()));
    }
}
