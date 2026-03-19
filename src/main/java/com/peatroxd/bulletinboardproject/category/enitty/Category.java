package com.peatroxd.bulletinboardproject.category.enitty;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "id", nullable = false)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    AdvertisementCategory category;

}