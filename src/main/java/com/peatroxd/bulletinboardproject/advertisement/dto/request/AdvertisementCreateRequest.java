        package com.peatroxd.bulletinboardproject.advertisement.dto.request;

        import com.peatroxd.bulletinboardproject.advertisement.enums.AdvertisementType;
        import io.swagger.v3.oas.annotations.media.Schema;
        import jakarta.validation.constraints.NotBlank;
        import jakarta.validation.constraints.NotNull;
        import jakarta.validation.constraints.Positive;
        import jakarta.validation.constraints.Size;

        import java.math.BigDecimal;

        @Schema(description = "Запрос на создание или обновление объявления")
        public record AdvertisementCreateRequest(

                @Schema(description = "Заголовок объявления", example = "Продам Toyota Camry", maxLength = 100)
                @NotBlank
                @Size(max = 100)
                String title,

                @Schema(description = "Описание объявления", example = "Автомобиль в хорошем состоянии, один владелец", maxLength = 2000)
                @NotBlank
                @Size(max = 2000)
                String description,

                @Schema(description = "Цена", example = "1250000.00")
                @NotNull
                @Positive
                BigDecimal price,

                @Schema(description = "Идентификатор категории", example = "10")
                @NotNull
                Long categoryId,

                @Schema(description = "Тип объявления", example = "SELL")
                @NotNull
                AdvertisementType advertisementType
        ) {
        }
