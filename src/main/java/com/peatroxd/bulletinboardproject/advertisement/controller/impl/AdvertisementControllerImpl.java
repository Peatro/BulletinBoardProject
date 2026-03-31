package com.peatroxd.bulletinboardproject.advertisement.controller.impl;

import com.peatroxd.bulletinboardproject.advertisement.controller.AdvertisementController;
import com.peatroxd.bulletinboardproject.advertisement.dto.request.AdvertisementCreateRequest;
import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.advertisement.service.AdvertisementService;
import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/advertisements")
public class AdvertisementControllerImpl implements AdvertisementController {

    private final AdvertisementService advertisementService;

    @Override
    public ResponseEntity<AdvertisementResponse> createAdvertisement(
            @RequestBody AdvertisementCreateRequest request,
            @CurrentUser UUID userId
    ) {
        return ResponseEntity.status(201)
                .body(advertisementService.createAdvertisement(request, userId));
    }

    @Override
    public ResponseEntity<AdvertisementResponse> getAdvertisementById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(advertisementService.getAdvertisementById(id));
    }

    @Override
    public ResponseEntity<List<AdvertisementResponse>> getAllAdvertisements() {
        return ResponseEntity.ok(advertisementService.getAllAdvertisements());
    }

    @Override
    public ResponseEntity<AdvertisementResponse> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody AdvertisementCreateRequest request,
            @CurrentUser UUID userId
    ) {
        return ResponseEntity.ok(advertisementService.updateAdvertisement(id, request, userId));
    }

    @Override
    public ResponseEntity<Void> deleteAdvertisement(
            @PathVariable Long id,
            @CurrentUser UUID userId
    ) {
        advertisementService.deleteAdvertisement(id, userId);
        return ResponseEntity.status(204).build();
    }

    @Override
    public ResponseEntity<AdvertisementResponse> publish(
            @PathVariable Long id,
            @CurrentUser UUID userId
    ) {
        return ResponseEntity.ok(advertisementService.publishAdvertisement(id, userId));
    }
}
