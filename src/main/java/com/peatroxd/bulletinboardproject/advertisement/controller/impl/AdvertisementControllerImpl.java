package com.peatroxd.bulletinboardproject.advertisement.controller.impl;

import com.peatroxd.bulletinboardproject.advertisement.controller.AdvertisementController;
import com.peatroxd.bulletinboardproject.advertisement.dto.request.CreateAdvertisementRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import com.peatroxd.bulletinboardproject.advertisement.mapper.AdvertisementMapper;
import com.peatroxd.bulletinboardproject.advertisement.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcements")
public class AdvertisementControllerImpl implements AdvertisementController {

    private final AdvertisementService advertisementService;
    private final AdvertisementMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Advertisement> list() {
        return advertisementService.list();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public AdvertisementResponse create(
            @Valid @RequestBody CreateAdvertisementRequest request,
            JwtAuthenticationToken token
    ) {

        UUID userId = UUID.fromString(token.getToken().getSubject());

        Advertisement advertisement = advertisementService.create(request, userId);

        return mapper.toAdvertisementResponse(advertisement);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #a.authorUsername == authentication.name")
    public Advertisement update(@PathVariable Long id, @RequestBody Advertisement a) {
        a.setId(id);
        return advertisementService.update(a);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #author == authentication.name")
    public void delete(@PathVariable Long id, @RequestParam String author) {
        advertisementService.delete(id);
    }
}
