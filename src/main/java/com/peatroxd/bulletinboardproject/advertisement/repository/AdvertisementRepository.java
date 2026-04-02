package com.peatroxd.bulletinboardproject.advertisement.repository;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.entity.Advertisement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findAllByAuthor_KeycloakUserId(UUID keycloakUserId);

    Optional<Advertisement> findByIdAndStatus(Long id, AdvertisementStatus status);

    @Query("""
            select a from Advertisement a
            where a.status = :status
              and (:categoryId is null or a.category.id = :categoryId)
              and (:authorId is null or a.author.keycloakUserId = :authorId)
            order by a.publishedAt desc, a.id desc
            """)
    List<Advertisement> findAllByPublicFilters(
            @Param("status") AdvertisementStatus status,
            @Param("categoryId") Long categoryId,
            @Param("authorId") UUID authorId
    );
}
