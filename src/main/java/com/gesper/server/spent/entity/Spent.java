package com.gesper.server.spent.entity;

import com.gesper.server.categorie.entity.Categorie;
import com.gesper.server.common.entity.BaseEntity;
import com.gesper.server.gain.entity.Gain;
import com.gesper.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

/**
 * Dépense d'un utilisateur. Utilise BigDecimal pour la précision monétaire.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "spents", indexes = {
        @Index(name = "idx_spents_user_id", columnList = "user_id"),
        @Index(name = "idx_spents_gain_id", columnList = "gain_id"),
        @Index(name = "idx_spents_categorie_id", columnList = "categorie_id")
})
@SQLDelete(sql = "UPDATE spents SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Spent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_spents_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gain_id", foreignKey = @ForeignKey(name = "fk_spents_gain"))
    private Gain gain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false, foreignKey = @ForeignKey(name = "fk_spents_categorie"))
    private Categorie categorie;

    @Column(name = "libelle", length = 55)
    private String libelle;

    @Column(name = "is_spent", nullable = false)
    @Builder.Default
    private boolean spent = true;

    /**
     * Montant. BigDecimal pour précision financière.
     */
    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
