package com.gesper.server.user.entity;

import com.gesper.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_clients_user"))
    private User user;

    /**
     * Si false, le compte est banni / désactivé.
     */
    @Column(name = "is_actif", nullable = false)
    @Builder.Default
    private boolean actif = true;
}
