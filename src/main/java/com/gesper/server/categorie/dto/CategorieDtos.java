package com.gesper.server.categorie.dto;

import com.gesper.server.categorie.entity.Categorie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

public class CategorieDtos {

    public record CategorieRequest(
            @NotBlank @Size(max = 100) String title,
            @NotBlank @Size(max = 500) String subtitle,
            Boolean isOrganized,
            Boolean isSpent
    ) {}

    public record CategorieResponse(
            Long id,
            String title,
            String subtitle,
            boolean isOrganized,
            boolean isSpent
    ) {}

    @Component
    public static class CategorieMapper {
        public CategorieResponse toResponse(Categorie c) {
            return new CategorieResponse(
                    c.getId(), c.getTitle(), c.getSubtitle(),
                    c.isOrganized(), c.isSpentCategory()
            );
        }

        public Categorie toEntity(CategorieRequest req) {
            return Categorie.builder()
                    .title(req.title())
                    .subtitle(req.subtitle())
                    .organized(Boolean.TRUE.equals(req.isOrganized()))
                    .spentCategory(req.isSpent() == null || req.isSpent())
                    .build();
        }

        public void update(Categorie target, CategorieRequest req) {
            target.setTitle(req.title());
            target.setSubtitle(req.subtitle());
            if (req.isOrganized() != null) target.setOrganized(req.isOrganized());
            if (req.isSpent() != null) target.setSpentCategory(req.isSpent());
        }
    }
}
