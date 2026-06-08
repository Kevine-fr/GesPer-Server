package com.gesper.server.auth.service;

import com.gesper.server.auth.entity.VerificationCode;
import com.gesper.server.auth.entity.VerificationCode.CodePurpose;
import com.gesper.server.auth.repository.VerificationCodeRepository;
import com.gesper.server.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long CODE_TTL_MINUTES = 10;
    private static final long THROTTLE_SECONDS = 15;

    private final VerificationCodeRepository repository;

    @Transactional
    public VerificationCode generate(String email, CodePurpose purpose) {
        // Throttling : un code peut être généré au max toutes les 15 secondes.
        repository.findFirstByEmailAndPurposeOrderByCreatedAtDesc(email, purpose).ifPresent(last -> {
            if (last.getCreatedAt().isAfter(Instant.now().minusSeconds(THROTTLE_SECONDS))) {
                throw new BadRequestException("Veuillez patienter quelques secondes avant de redemander un code.");
            }
        });

        String code = String.format("%06d", 100000 + RANDOM.nextInt(900000));
        VerificationCode entity = VerificationCode.builder()
                .email(email)
                .code(code)
                .purpose(purpose)
                .expiresAt(Instant.now().plus(CODE_TTL_MINUTES, ChronoUnit.MINUTES))
                .used(false)
                .build();
        return repository.save(entity);
    }

    @Transactional
    public void verify(String email, String code, CodePurpose purpose) {
        VerificationCode vc = repository
                .findFirstByEmailAndCodeAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, code, purpose)
                .orElseThrow(() -> new BadRequestException("Code de vérification invalide."));

        if (vc.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Le code de vérification a expiré.");
        }
        vc.setUsed(true);
        repository.save(vc);
    }
}
