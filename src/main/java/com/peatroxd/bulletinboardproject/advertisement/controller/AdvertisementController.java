package com.peatroxd.bulletinboardproject.advertisement.controller;

import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public interface AdvertisementController {

    @PostMapping
    ResponseEntity<AdvertisementResponse> createAdvertisement(
            @RequestBody AdvertisementCreateRequest request,
            @CurrentUser UUID userId
    );

    @GetMapping("/{id}")
    ResponseEntity<AdvertisementResponse> getAdvertisementById(
            @PathVariable Long id
    );

    @GetMapping
    ResponseEntity<List<AdvertisementResponse>> getAllAdvertisements();

    @PutMapping("/{id}")
    ResponseEntity<AdvertisementResponse> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody AdvertisementCreateRequest request,
            @CurrentUser UUID userId
    );

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAdvertisement(
            @PathVariable Long id,
            @CurrentUser UUID userId
    );

    @PatchMapping("/{id}")
    ResponseEntity<AdvertisementResponse> publish(
            @PathVariable Long id,
            @CurrentUser UUID userId
    );
}
