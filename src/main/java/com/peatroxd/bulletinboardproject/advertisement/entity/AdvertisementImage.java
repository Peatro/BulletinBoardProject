package com.peatroxd.bulletinboardproject.advertisement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "advertisement_image")
public class AdvertisementImage {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

}