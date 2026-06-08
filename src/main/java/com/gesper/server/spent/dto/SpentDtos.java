package com.gesper.server.spent.dto;

import com.gesper.server.spent.entity.Spent;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

public class SpentDtos {

    public record CreateSpentRequest(
            Long gainId,
            @NotNull Long categorieId,
            @NotBlank @Size(max = 55) String libelle,
            @NotNull @DecimalMin("0.00") BigDecimal value,
            @NotNull Boolean isSpent
    ) {}

    public record CreateSpentByAdminRequest(
            @NotNull Long userId,
            Long gainId,
            @NotNull Long categorieId,
            @NotBlank @Size(max = 55) String libelle,
            @NotNull @DecimalMin("0.00") BigDecimal value,
            @NotNull Boolean isSpent
    ) {}

    public record UpdateSpentRequest(
            Long gainId,
            @NotNull Long categorieId,
            @NotBlank @Size(max = 55) String libelle,
            @NotNull @DecimalMin("0.00") BigDecimal value,
            @NotNull Boolean isSpent
    ) {}

    public record SpentResponse(
            Long id,
            Long userId,
            Long gainId,
            Long categorieId,
            String libelle,
            BigDecimal value,
            boolean isSpent,
            Instant createdAt,
            Instant updatedAt
    ) {}

    @Component
    public static class SpentMapper {
        public SpentResponse toResponse(Spent s) {
            return new SpentResponse(
                    s.getId(),
                    s.getUser().getId(),
                    s.getGain() != null ? s.getGain().getId() : null,
                    s.getCategorie().getId(),
                    s.getLibelle(),
                    s.getValue(),
                    s.isSpent(),
                    s.getCreatedAt(),
                    s.getUpdatedAt()
            );
        }
    }
}
