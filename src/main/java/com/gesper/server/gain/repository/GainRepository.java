package com.gesper.server.gain.repository;

import com.gesper.server.gain.entity.Gain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GainRepository extends JpaRepository<Gain, Long> {

    Page<Gain> findByUserId(Long userId, Pageable pageable);

    Optional<Gain> findByIdAndUserId(Long id, Long userId);
}
