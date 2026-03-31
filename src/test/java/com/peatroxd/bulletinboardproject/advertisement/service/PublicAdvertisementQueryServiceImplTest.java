package com.peatroxd.bulletinboardproject.advertisement.service;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.PublicAdvertisementFilter;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.mapper.AdvertisementMapper;
import com.peatroxd.bulletinboardproject.advertisement.repository.AdvertisementRepository;
import com.peatroxd.bulletinboardproject.advertisement.service.impl.PublicAdvertisementQueryServiceImpl;
import com.peatroxd.bulletinboardproject.common.exception.BadRequestException;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicAdvertisementQueryServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final Long ADVERTISEMENT_ID = 42L;
    private static final Long CATEGORY_ID = 10L;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @InjectMocks
    private PublicAdvertisementQueryServiceImpl publicAdvertisementQueryService;

    @Test
    void getAdvertisementByIdShouldThrowWhenAdvertisementIsMissing() {
        when(advertisementRepository.findByIdAndStatus(99L, AdvertisementStatus.PUBLISHED)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicAdvertisementQueryService.getAdvertisementById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Advertisement not found.");
    }

    @Test
    void getAdvertisementByIdShouldReturnOnlyPublishedAdvertisement() {
        Advertisement published = advertisement(AdvertisementStatus.PUBLISHED, author(USER_ID));
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(ADVERTISEMENT_ID)
                .status(AdvertisementStatus.PUBLISHED)
                .build();

        when(advertisementRepository.findByIdAndStatus(ADVERTISEMENT_ID, AdvertisementStatus.PUBLISHED))
                .thenReturn(Optional.of(published));
        when(advertisementMapper.toResponse(published)).thenReturn(response);

        AdvertisementResponse actual = publicAdvertisementQueryService.getAdvertisementById(ADVERTISEMENT_ID);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void getAllAdvertisementsShouldReturnPublishedAdvertisementsByDefault() {
        Advertisement published = advertisement(AdvertisementStatus.PUBLISHED, author(USER_ID));
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(ADVERTISEMENT_ID)
                .status(AdvertisementStatus.PUBLISHED)
                .build();

        when(advertisementRepository.findAllByPublicFilters(AdvertisementStatus.PUBLISHED, null, null))
                .thenReturn(List.of(published));
        when(advertisementMapper.toResponse(published)).thenReturn(response);

        List<AdvertisementResponse> actual = publicAdvertisementQueryService.getAllAdvertisements(
                new PublicAdvertisementFilter(null, null, null)
        );

        assertThat(actual).containsExactly(response);
    }

    @Test
    void getAllAdvertisementsShouldApplyProvidedFilters() {
        Advertisement published = advertisement(AdvertisementStatus.PUBLISHED, author(USER_ID));
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(ADVERTISEMENT_ID)
                .categoryId(CATEGORY_ID)
                .authorId(USER_ID)
                .status(AdvertisementStatus.PUBLISHED)
                .build();

        when(advertisementRepository.findAllByPublicFilters(AdvertisementStatus.PUBLISHED, CATEGORY_ID, USER_ID))
                .thenReturn(List.of(published));
        when(advertisementMapper.toResponse(published)).thenReturn(response);

        List<AdvertisementResponse> actual = publicAdvertisementQueryService.getAllAdvertisements(
                new PublicAdvertisementFilter(CATEGORY_ID, AdvertisementStatus.PUBLISHED, USER_ID)
        );

        assertThat(actual).containsExactly(response);
    }

    @Test
    void getAllAdvertisementsShouldRejectNonPublishedPublicStatusFilter() {
        assertThatThrownBy(() -> publicAdvertisementQueryService.getAllAdvertisements(
                new PublicAdvertisementFilter(null, AdvertisementStatus.DRAFT, null)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Public list supports only PUBLISHED status");
    }

    private User author(UUID keycloakUserId) {
        return User.builder()
                .id(UUID.randomUUID())
                .keycloakUserId(keycloakUserId)
                .username("alice")
                .phone("+70000000000")
                .build();
    }

    private Advertisement advertisement(AdvertisementStatus status, User author) {
        return Advertisement.builder()
                .id(ADVERTISEMENT_ID)
                .author(author)
                .status(status)
                .build();
    }
}
