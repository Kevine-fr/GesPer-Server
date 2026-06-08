package com.gesper.server.gain.dto;

import com.gesper.server.gain.entity.Gain;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

public class GainDtos {

    public record CreateGainRequest(
            @NotNull(message = "categorieId est requis") Long categorieId,
            @NotBlank @Size(max = 55) String libelle,
            @NotNull @DecimalMin(value = "0.00", inclusive = true, message = "Le montant doit être positif") BigDecimal sum,
            @NotNull Boolean isReccurent
    ) {}

    public record CreateGainByAdminRequest(
            @NotNull Long userId,
            @NotNull Long categorieId,
            @NotBlank @Size(max = 55) String libelle,
            @NotNull @DecimalMin("0.00") BigDecimal sum,
            @NotNull Boolean isReccurent
    ) {}

    public record UpdateGainRequest(
            @NotNull Long categorieId,
            @NotBlank @Size(max = 55) String libelle,
            @NotNull @DecimalMin("0.00") BigDecimal sum,
            @NotNull Boolean isReccurent
    ) {}

    public record GainResponse(
            Long id,
            Long userId,
            Long categorieId,
            String libelle,
            BigDecimal sum,
            boolean isReccurent,
            Instant createdAt,
            Instant updatedAt
    ) {}

    @Component
    public static class GainMapper {
        public GainResponse toResponse(Gain g) {
            return new GainResponse(
                    g.getId(),
                    g.getUser().getId(),
                    g.getCategorie().getId(),
                    g.getLibelle(),
                    g.getSum(),
                    g.isRecurrent(),
                    g.getCreatedAt(),
                    g.getUpdatedAt()
            );
        }
    }
}
