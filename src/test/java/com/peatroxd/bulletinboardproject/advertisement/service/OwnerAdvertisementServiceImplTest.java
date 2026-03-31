package com.peatroxd.bulletinboardproject.advertisement.service;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import com.peatroxd.bulletinboardproject.advertisement.mapper.AdvertisementMapper;
import com.peatroxd.bulletinboardproject.advertisement.repository.AdvertisementRepository;
import com.peatroxd.bulletinboardproject.advertisement.service.impl.OwnerAdvertisementServiceImpl;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.facade.CategoryFacade;
import com.peatroxd.bulletinboardproject.common.exception.BadRequestException;
import com.peatroxd.bulletinboardproject.common.exception.ForbiddenOperationException;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerAdvertisementServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final Long ADVERTISEMENT_ID = 42L;
    private static final Long CATEGORY_ID = 10L;
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private CategoryFacade categoryFacade;

    @Mock
    private UserFacade userFacade;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @InjectMocks
    private OwnerAdvertisementServiceImpl ownerAdvertisementService;

    @Test
    void createAdvertisementShouldPersistDraftWithAuthorAndCategory() {
        User author = author(USER_ID);
        Category category = category();
        AdvertisementCreateRequest request = createRequest();
        Advertisement mappedEntity = mappedAdvertisement(request);
        Advertisement savedEntity = Advertisement.builder()
                .id(55L)
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .type(request.advertisementType())
                .status(AdvertisementStatus.DRAFT)
                .author(author)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(55L)
                .title("Title")
                .status(AdvertisementStatus.DRAFT)
                .type(AdvertisementType.SELL)
                .authorId(author.getId())
                .categoryId(category.getId())
                .build();

        when(userFacade.getByKeycloakId(USER_ID)).thenReturn(author);
        when(categoryFacade.getById(CATEGORY_ID)).thenReturn(category);
        when(advertisementMapper.toEntity(request)).thenReturn(mappedEntity);
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(savedEntity);
        when(advertisementMapper.toResponse(savedEntity)).thenReturn(response);

        AdvertisementResponse actual = ownerAdvertisementService.createAdvertisement(request, USER_ID);

        assertThat(actual).isEqualTo(response);

        ArgumentCaptor<Advertisement> captor = ArgumentCaptor.forClass(Advertisement.class);
        verify(advertisementRepository).save(captor.capture());
        Advertisement persisted = captor.getValue();
        assertThat(persisted.getAuthor()).isEqualTo(author);
        assertThat(persisted.getCategory()).isEqualTo(category);
        assertThat(persisted.getStatus()).isEqualTo(AdvertisementStatus.DRAFT);
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
    }

    @Test
    void getAllAdvertisementsByUserIdShouldMapRepositoryResult() {
        Advertisement first = Advertisement.builder().id(1L).build();
        Advertisement second = Advertisement.builder().id(2L).build();
        AdvertisementResponse firstResponse = AdvertisementResponse.builder().id(1L).build();
        AdvertisementResponse secondResponse = AdvertisementResponse.builder().id(2L).build();

        when(advertisementRepository.findAllByAuthor_KeycloakUserId(USER_ID))
                .thenReturn(List.of(first, second));
        when(advertisementMapper.toResponse(first)).thenReturn(firstResponse);
        when(advertisementMapper.toResponse(second)).thenReturn(secondResponse);

        List<AdvertisementResponse> result = ownerAdvertisementService.getAllAdvertisementsByUserId(USER_ID);

        assertThat(result).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void deleteAdvertisementShouldDeleteOwnedEntity() {
        Advertisement advertisement = advertisement(AdvertisementStatus.DRAFT, author(USER_ID));
        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));

        ownerAdvertisementService.deleteAdvertisement(ADVERTISEMENT_ID, USER_ID);

        verify(advertisementRepository).delete(advertisement);
    }

    @Test
    void deleteAdvertisementShouldRejectForeignOwner() {
        Advertisement advertisement = advertisement(AdvertisementStatus.DRAFT, author(UUID.randomUUID()));
        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));

        assertThatThrownBy(() -> ownerAdvertisementService.deleteAdvertisement(ADVERTISEMENT_ID, USER_ID))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage("Forbidden");

        verify(advertisementRepository, never()).delete(any());
    }

    @Test
    void updateAdvertisementShouldOverwriteEditableFieldsAndCategory() {
        AdvertisementCreateRequest request = new AdvertisementCreateRequest(
                "Updated title",
                "Updated description",
                BigDecimal.valueOf(2000),
                CATEGORY_ID,
                AdvertisementType.BUY
        );
        Category category = category();
        Advertisement advertisement = advertisement(AdvertisementStatus.DRAFT, author(USER_ID));
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(ADVERTISEMENT_ID)
                .title("Updated title")
                .type(AdvertisementType.BUY)
                .categoryId(CATEGORY_ID)
                .build();

        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));
        when(categoryFacade.getById(CATEGORY_ID)).thenReturn(category);
        when(advertisementRepository.save(advertisement)).thenReturn(advertisement);
        when(advertisementMapper.toResponse(advertisement)).thenReturn(response);

        AdvertisementResponse actual = ownerAdvertisementService.updateAdvertisement(ADVERTISEMENT_ID, request, USER_ID);

        assertThat(actual).isEqualTo(response);
        assertThat(advertisement.getTitle()).isEqualTo("Updated title");
        assertThat(advertisement.getDescription()).isEqualTo("Updated description");
        assertThat(advertisement.getPrice()).isEqualByComparingTo("2000");
        assertThat(advertisement.getType()).isEqualTo(AdvertisementType.BUY);
        assertThat(advertisement.getCategory()).isEqualTo(category);
        assertThat(advertisement.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateAdvertisementShouldRejectForeignOwner() {
        AdvertisementCreateRequest request = createRequest();
        Advertisement advertisement = advertisement(AdvertisementStatus.DRAFT, author(UUID.randomUUID()));

        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));

        assertThatThrownBy(() -> ownerAdvertisementService.updateAdvertisement(ADVERTISEMENT_ID, request, USER_ID))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage("Forbidden");

        verify(advertisementRepository, never()).save(any());
    }

    @Test
    void publishAdvertisementShouldPublishDraftOwnedByCurrentUser() {
        User author = author(USER_ID);
        Advertisement advertisement = advertisement(AdvertisementStatus.DRAFT, author);
        Advertisement saved = Advertisement.builder()
                .id(ADVERTISEMENT_ID)
                .author(author)
                .status(AdvertisementStatus.PUBLISHED)
                .publishedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(ADVERTISEMENT_ID)
                .status(AdvertisementStatus.PUBLISHED)
                .build();

        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));
        when(advertisementRepository.save(advertisement)).thenReturn(saved);
        when(advertisementMapper.toResponse(saved)).thenReturn(response);

        AdvertisementResponse actual = ownerAdvertisementService.publishAdvertisement(ADVERTISEMENT_ID, USER_ID);

        assertThat(actual.status()).isEqualTo(AdvertisementStatus.PUBLISHED);
        assertThat(advertisement.getStatus()).isEqualTo(AdvertisementStatus.PUBLISHED);
        assertThat(advertisement.getPublishedAt()).isNotNull();
        assertThat(advertisement.getUpdatedAt()).isNotNull();
    }

    @Test
    void publishAdvertisementShouldRejectForeignOwner() {
        Advertisement advertisement = advertisement(AdvertisementStatus.DRAFT, author(UUID.randomUUID()));

        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));

        assertThatThrownBy(() -> ownerAdvertisementService.publishAdvertisement(ADVERTISEMENT_ID, USER_ID))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage("Forbidden");

        verify(advertisementRepository, never()).save(any());
    }

    @Test
    void publishAdvertisementShouldRejectNonDraftStatus() {
        Advertisement advertisement = advertisement(AdvertisementStatus.PUBLISHED, author(USER_ID));

        when(advertisementRepository.findById(ADVERTISEMENT_ID)).thenReturn(Optional.of(advertisement));

        assertThatThrownBy(() -> ownerAdvertisementService.publishAdvertisement(ADVERTISEMENT_ID, USER_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only DRAFT can be published");

        verify(advertisementRepository, never()).save(any());
    }

    private AdvertisementCreateRequest createRequest() {
        return new AdvertisementCreateRequest(
                TITLE,
                DESCRIPTION,
                BigDecimal.valueOf(1000),
                CATEGORY_ID,
                AdvertisementType.SELL
        );
    }

    private Advertisement mappedAdvertisement(AdvertisementCreateRequest request) {
        return Advertisement.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .type(request.advertisementType())
                .build();
    }

    private User author(UUID keycloakUserId) {
        return User.builder()
                .id(UUID.randomUUID())
                .keycloakUserId(keycloakUserId)
                .username("alice")
                .phone("+70000000000")
                .build();
    }

    private Category category() {
        return Category.builder()
                .id(CATEGORY_ID)
                .name("Cars")
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
