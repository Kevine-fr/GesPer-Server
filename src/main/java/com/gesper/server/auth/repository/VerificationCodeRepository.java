package com.gesper.server.auth.repository;

import com.gesper.server.auth.entity.VerificationCode;
import com.gesper.server.auth.entity.VerificationCode.CodePurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findFirstByEmailAndCodeAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            String email, String code, CodePurpose purpose);

    Optional<VerificationCode> findFirstByEmailAndPurposeOrderByCreatedAtDesc(String email, CodePurpose purpose);

    @Modifying
    @Query("DELETE FROM VerificationCode vc WHERE vc.expiresAt < :now OR vc.used = true")
    void deleteExpiredOrUsed(@Param("now") Instant now);
}
