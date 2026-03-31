package com.peatroxd.bulletinboardproject.advertisement.controller;

import com.peatroxd.bulletinboardproject.advertisement.controller.impl.AdvertisementControllerImpl;
import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import com.peatroxd.bulletinboardproject.advertisement.service.AdvertisementService;
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
    private AdvertisementService advertisementService;

    @InjectMocks
    private AdvertisementControllerImpl advertisementController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(advertisementController)
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllAdvertisementsShouldReturnOk() throws Exception {
        when(advertisementService.getAllAdvertisements()).thenReturn(List.of(
                AdvertisementResponse.builder().id(1L).title("First").build(),
                AdvertisementResponse.builder().id(2L).title("Second").build()
        ));

        mockMvc.perform(get("/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].title").value("Second"));

        verify(advertisementService).getAllAdvertisements();
    }

    @Test
    void createAdvertisementShouldReturnCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);

        AdvertisementCreateRequest request = createRequest();
        AdvertisementResponse response = draftResponse(55L);

        when(advertisementService.createAdvertisement(request, userId)).thenReturn(response);

        mockMvc.perform(post("/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(advertisementService).createAdvertisement(request, userId);
    }

    @Test
    void updateAdvertisementShouldBeAvailableViaPutWithId() throws Exception {
        mockMvc.perform(put("/advertisements/{id}", ADVERTISEMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ADVERTISEMENT_ID));
    }

    @Test
    void deleteAdvertisementShouldBeAvailableViaDeleteWithId() throws Exception {
        mockMvc.perform(delete("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isNoContent());

        verify(advertisementService).deleteAdvertisement(ADVERTISEMENT_ID);
    }

    @Test
    void publishAdvertisementShouldBeAvailableViaPatchWithId() throws Exception {
        UUID userId = UUID.randomUUID();
        setCurrentJwtUser(userId);
        AdvertisementResponse response = publishedResponse(ADVERTISEMENT_ID);

        when(advertisementService.publishAdvertisement(ADVERTISEMENT_ID, userId)).thenReturn(response);

        mockMvc.perform(patch("/advertisements/{id}", ADVERTISEMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(advertisementService).publishAdvertisement(ADVERTISEMENT_ID, userId);
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
