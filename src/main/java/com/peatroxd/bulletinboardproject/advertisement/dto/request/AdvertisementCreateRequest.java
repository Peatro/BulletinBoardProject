        package com.peatroxd.bulletinboardproject.advertisement.dto.request;

        import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
        import jakarta.validation.constraints.NotBlank;
        import jakarta.validation.constraints.NotNull;
        import jakarta.validation.constraints.Positive;
        import jakarta.validation.constraints.Size;

        import java.math.BigDecimal;

        public record AdvertisementCreateRequest(

                @NotBlank
                @Size(max = 100)
                String title,

                @NotBlank
                @Size(max = 2000)
                String description,

                @NotNull
                @Positive
                BigDecimal price,

                @NotNull
                Long categoryId,

                @NotNull
                AdvertisementType advertisementType
        ) {
        }
