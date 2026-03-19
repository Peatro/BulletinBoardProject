package com.peatroxd.bulletinboardproject.advertisement.repository;

import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
}
