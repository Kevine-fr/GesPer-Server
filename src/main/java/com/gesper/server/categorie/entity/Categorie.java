package com.gesper.server.categorie.entity;

import com.gesper.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Categorie extends BaseEntity {

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "subtitle", nullable = false, length = 500)
    private String subtitle;

    /**
     * Catégorie système organisée (non éditable par l'utilisateur).
     */
    @Column(name = "is_organized", nullable = false)
    @Builder.Default
    private boolean organized = false;

    /**
     * true = catégorie de dépense, false = catégorie de gain/revenu.
     */
    @Column(name = "is_spent", nullable = false)
    @Builder.Default
    private boolean spentCategory = true;
}
