package com.peatroxd.bulletinboardproject.advertisement.controller;

import com.peatroxd.bulletinboardproject.advertisement.dto.response.AdvertisementResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface AdvertisementController {

    AdvertisementResponse publish(@PathVariable Long id);
}
