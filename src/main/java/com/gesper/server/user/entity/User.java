package com.gesper.server.user.entity;

import com.gesper.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true)
})
public class User extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Hash du mot de passe (BCrypt). Peut être null pour les utilisateurs OAuth2.
     */
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 20)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    /**
     * Identifiant du provider externe (sub Google, ...).
     */
    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "profile_picture_url", length = 512)
    private String profilePictureUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_role"))
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Client client;

    /**
     * Active après vérification email/admin.
     */
    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;
}
