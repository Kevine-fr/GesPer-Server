package com.gesper.server.gain.entity;

import com.gesper.server.categorie.entity.Categorie;
import com.gesper.server.common.entity.BaseEntity;
import com.gesper.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

/**
 * Gain (revenu) d'un utilisateur. Utilise BigDecimal pour la précision monétaire.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gains", indexes = {
        @Index(name = "idx_gains_user_id", columnList = "user_id"),
        @Index(name = "idx_gains_categorie_id", columnList = "categorie_id")
})
@SQLDelete(sql = "UPDATE gains SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Gain extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_gains_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false, foreignKey = @ForeignKey(name = "fk_gains_categorie"))
    private Categorie categorie;

    @Column(name = "libelle", length = 55)
    private String libelle;

    /**
     * Montant. BigDecimal avec précision adaptée au financier (15 chiffres, 2 décimales).
     */
    @Column(name = "sum", nullable = false, precision = 15, scale = 2)
    private BigDecimal sum;

    @Column(name = "is_recurrent", nullable = false)
    @Builder.Default
    private boolean recurrent = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
