package com.peatroxd.bulletinboardproject.advertisement.repository;

import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findAllByAuthor_KeycloakUserId(UUID keycloakUserId);
}
