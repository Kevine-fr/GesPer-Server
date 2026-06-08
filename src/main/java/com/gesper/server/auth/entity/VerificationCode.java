package com.gesper.server.auth.entity;

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
@Table(name = "verification_codes", indexes = {
        @Index(name = "idx_verification_codes_email", columnList = "email")
})
public class VerificationCode extends BaseEntity {

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    private CodePurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private boolean used = false;

    public enum CodePurpose {
        REGISTER_ADMIN,
        REGISTER_CLIENT,
        PASSWORD_RESET
    }
}
