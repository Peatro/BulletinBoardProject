package com.peatroxd.bulletinboardproject.advertisement.controller;

import com.peatroxd.bulletinboardproject.advertisement.controller.impl.AdvertisementControllerImpl;
import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.request.PublicAdvertisementFilter;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import com.peatroxd.bulletinboardproject.advertisement.service.OwnerAdvertisementService;
import com.peatroxd.bulletinboardproject.advertisement.service.PublicAdvertisementQueryService;
import com.peatroxd.bulletinboardproject.common.exception.BadRequestException;
import com.peatroxd.bulletinboardproject.common.exception.ForbiddenOperationException;
import com.peatroxd.bulletinboardproject.common.exception.GlobalExceptionHandler;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import com.peatroxd.bulletinboardproject.security.service.CurrentUserArgumentResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdvertisementControllerWebMvcTest {

    private static final Long ADVERTISEMENT_ID = 42L;
    private static final Long CATEGORY_ID = 10L;
    private static final String CREATE_REQUEST_JSON = """
            {
              "title": "Title",
              "description": "Description",
              "price": 1000,
              "categoryId": 10,
              "advertisementType": "SELL"
            }
            """;

    private static final String UPDATE_REQUEST_JSON = """
            {
              "title": "Updated title",
              "description": "Updated description",
              "price": 2000,
              "categoryId": 10,
              "advertisementType": "SELL"
            }
            """;

    @Mock
    private PublicAdvertisementQueryService publicAdvertisementQueryService;

    @Mock
    private OwnerAdvertisementService ownerAdvertisementService;

    @InjectMocks
    private AdvertisementControllerImpl advertisementController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(advertisementController)
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllAdvertisementsShouldReturnOk() throws Exception {
        when(publicAdvertisementQueryService.getAllAdvertisements(new PublicAdvertisementFilter(null, null, null))).thenReturn(List.of(
                AdvertisementResponse.builder().id(1L).title("First").build(),
                AdvertisementResponse.builder().id(2L).title("Second").build()
        ));

        mockMvc.perform(get("/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].title").value("Second"));

        verify(publicAdvertisementQueryService).getAllAdvertisements(new PublicAdvertisementFilter(null, null, null));
    }

    @Test
    void getAllAdvertisementsShouldPassFiltersToService() throws Exception {
        UUID authorId = UUID.randomUUID();
        PublicAdvertisementFilter filter = new PublicAdvertisementFilter(CATEGORY_ID, AdvertisementStatus.PUBLISHED, authorId);

        when(publicAdvertisementQueryService.getAllAdvertisements(filter))
                .thenReturn(List.of(AdvertisementResponse.builder().id(1L).build()));

        mockMvc.perform(get("/advertisements")
                        .param("categoryId", CATEGORY_ID.toString())
                        .param("status", "PUBLISHED")
                        .param("authorId", authorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(publicAdvertisementQueryService).getAllAdvertisements(filter);
    }

    @Test
    void getCurrentUserAdvertisementsShouldReturnOnlyAuthenticatedUserItems() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);

        when(ownerAdvertisementService.getAllAdvertisementsByUserId(userId)).thenReturn(List.of(
                AdvertisementResponse.builder().id(10L).title("My first").build(),
                AdvertisementResponse.builder().id(11L).title("My second").build()
        ));

        mockMvc.perform(get("/advertisements/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[1].title").value("My second"));

        verify(ownerAdvertisementService).getAllAdvertisementsByUserId(userId);
    }

    @Test
    void createAdvertisementShouldReturnCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);

        AdvertisementCreateRequest request = createRequest();
        AdvertisementResponse response = draftResponse(55L);

        when(ownerAdvertisementService.createAdvertisement(request, userId)).thenReturn(response);

        mockMvc.perform(post("/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(ownerAdvertisementService).createAdvertisement(request, userId);
    }

    @Test
    void updateAdvertisementShouldBeAvailableViaPutWithId() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);
        AdvertisementCreateRequest request = new AdvertisementCreateRequest(
                "Updated title",
                "Updated description",
                BigDecimal.valueOf(2000),
                CATEGORY_ID,
                AdvertisementType.SELL
        );
        AdvertisementResponse response = AdvertisementResponse.builder()
                .id(ADVERTISEMENT_ID)
                .title("Updated title")
                .build();

        when(ownerAdvertisementService.updateAdvertisement(ADVERTISEMENT_ID, request, userId)).thenReturn(response);

        mockMvc.perform(put("/advertisements/{id}", ADVERTISEMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ADVERTISEMENT_ID));

        verify(ownerAdvertisementService).updateAdvertisement(ADVERTISEMENT_ID, request, userId);
    }

    @Test
    void deleteAdvertisementShouldBeAvailableViaDeleteWithId() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);

        mockMvc.perform(delete("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isNoContent());

        verify(ownerAdvertisementService).deleteAdvertisement(ADVERTISEMENT_ID, userId);
    }

    @Test
    void publishAdvertisementShouldBeAvailableViaPatchWithId() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);
        AdvertisementResponse response = publishedResponse(ADVERTISEMENT_ID);

        when(ownerAdvertisementService.publishAdvertisement(ADVERTISEMENT_ID, userId)).thenReturn(response);

        mockMvc.perform(patch("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(ownerAdvertisementService).publishAdvertisement(ADVERTISEMENT_ID, userId);
    }

    @Test
    void getAdvertisementByIdShouldReturn404WhenAdvertisementIsMissing() throws Exception {
        when(publicAdvertisementQueryService.getAdvertisementById(ADVERTISEMENT_ID))
                .thenThrow(new ResourceNotFoundException("Advertisement not found."));

        mockMvc.perform(get("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Advertisement not found."));
    }

    @Test
    void deleteAdvertisementShouldReturn403WhenCurrentUserIsNotOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);

        doThrow(new ForbiddenOperationException("Forbidden"))
                .when(ownerAdvertisementService).deleteAdvertisement(ADVERTISEMENT_ID, userId);

        mockMvc.perform(delete("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    void publishAdvertisementShouldReturn400WhenAdvertisementStatusIsInvalid() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);

        when(ownerAdvertisementService.publishAdvertisement(ADVERTISEMENT_ID, userId))
                .thenThrow(new BadRequestException("Only DRAFT can be published"));

        mockMvc.perform(patch("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Only DRAFT can be published"));
    }

    private void setCurrentJwtUser(UUID userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(userId.toString())
                .claim("scope", "openid")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwt, null, List.of())
        );
    }

    private AdvertisementCreateRequest createRequest() {
        return new AdvertisementCreateRequest(
                "Title",
                "Description",
                BigDecimal.valueOf(1000),
                CATEGORY_ID,
                AdvertisementType.SELL
        );
    }

    private AdvertisementResponse draftResponse(Long id) {
        return AdvertisementResponse.builder()
                .id(id)
                .title("Title")
                .status(AdvertisementStatus.DRAFT)
                .build();
    }

    private AdvertisementResponse publishedResponse(Long id) {
        return AdvertisementResponse.builder()
                .id(id)
                .status(AdvertisementStatus.PUBLISHED)
                .build();
    }
}
