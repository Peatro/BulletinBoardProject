package com.peatroxd.bulletinboardproject.advertisement.entity;

import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementStatus;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementSubCategory;
import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.currency.entity.Currency;
import com.peatroxd.bulletinboardproject.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private BigDecimal price;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false, columnDefinition = "uuid")
    @ToString.Exclude
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Category category;

    @Enumerated(EnumType.STRING)
    private AdvertisementSubCategory subCategory;

    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;

    @Enumerated(EnumType.STRING)
    private AdvertisementType type;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    private boolean published;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Advertisement that = (Advertisement) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
