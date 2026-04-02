package com.peatroxd.bulletinboardproject.advertisement.repository;

import com.peatroxd.bulletinboardproject.AbstractPostgresContainerTest;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.repository.CategoryRepository;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AdvertisementRepositoryTest extends AbstractPostgresContainerTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User firstAuthor;
    private User secondAuthor;
    private Category cars;
    private Category phones;

    @BeforeEach
    void setUp() {
        advertisementRepository.deleteAll();
        userRepository.deleteAll();

        cars = categoryRepository.findById(101L).orElseThrow();
        phones = categoryRepository.findById(301L).orElseThrow();

        firstAuthor = userRepository.save(user("alice", UUID.randomUUID()));
        secondAuthor = userRepository.save(user("bob", UUID.randomUUID()));
    }

    @Test
    void findAllByAuthorKeycloakUserIdShouldReturnOnlyAuthorsAdvertisements() {
        Advertisement first = advertisement("First", AdvertisementStatus.DRAFT, firstAuthor, cars, null);
        Advertisement second = advertisement("Second", AdvertisementStatus.PUBLISHED, secondAuthor, cars, LocalDateTime.now());

        advertisementRepository.saveAll(List.of(first, second));

        List<Advertisement> result = advertisementRepository.findAllByAuthor_KeycloakUserId(firstAuthor.getKeycloakUserId());

        assertThat(result)
                .extracting(Advertisement::getTitle)
                .containsExactly("First");
    }

    @Test
    void findByIdAndStatusShouldReturnOnlyMatchingStatus() {
        Advertisement draft = advertisement("Draft", AdvertisementStatus.DRAFT, firstAuthor, cars, null);
        Advertisement saved = advertisementRepository.save(draft);

        assertThat(advertisementRepository.findByIdAndStatus(saved.getId(), AdvertisementStatus.PUBLISHED)).isEmpty();
        assertThat(advertisementRepository.findByIdAndStatus(saved.getId(), AdvertisementStatus.DRAFT)).isPresent();
    }

    @Test
    void findAllByPublicFiltersShouldApplyStatusCategoryAndAuthorOrdering() {
        Advertisement newestMatch = advertisement(
                "Newest match",
                AdvertisementStatus.PUBLISHED,
                firstAuthor,
                cars,
                LocalDateTime.now().plusMinutes(1)
        );
        Advertisement olderMatch = advertisement(
                "Older match",
                AdvertisementStatus.PUBLISHED,
                firstAuthor,
                cars,
                LocalDateTime.now()
        );
        Advertisement wrongCategory = advertisement(
                "Wrong category",
                AdvertisementStatus.PUBLISHED,
                firstAuthor,
                phones,
                LocalDateTime.now().plusMinutes(2)
        );
        Advertisement wrongAuthor = advertisement(
                "Wrong author",
                AdvertisementStatus.PUBLISHED,
                secondAuthor,
                cars,
                LocalDateTime.now().plusMinutes(3)
        );
        Advertisement draft = advertisement(
                "Draft",
                AdvertisementStatus.DRAFT,
                firstAuthor,
                cars,
                null
        );

        advertisementRepository.saveAll(List.of(newestMatch, olderMatch, wrongCategory, wrongAuthor, draft));

        List<Advertisement> result = advertisementRepository.findAllByPublicFilters(
                AdvertisementStatus.PUBLISHED,
                cars.getId(),
                firstAuthor.getKeycloakUserId()
        );

        assertThat(result)
                .extracting(Advertisement::getTitle)
                .containsExactly("Newest match", "Older match");
    }

    private User user(String username, UUID keycloakUserId) {
        return User.builder()
                .username(username)
                .email(username + "@example.com")
                .firstName(username)
                .role(Role.USER)
                .enabled(true)
                .keycloakUserId(keycloakUserId)
                .build();
    }

    private Advertisement advertisement(
            String title,
            AdvertisementStatus status,
            User author,
            Category category,
            LocalDateTime publishedAt
    ) {
        return Advertisement.builder()
                .title(title)
                .description(title + " description")
                .price(BigDecimal.valueOf(1000))
                .status(status)
                .type(AdvertisementType.SELL)
                .author(author)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .publishedAt(publishedAt)
                .build();
    }
}
