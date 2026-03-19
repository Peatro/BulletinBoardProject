package com.peatroxd.bulletinboardproject.advertisement.controller;

import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface AdvertisementController {

    AdvertisementResponse publish(@PathVariable Long id, @CurrentUser UUID userId);
}
