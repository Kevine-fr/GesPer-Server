package com.gesper.server.spent.repository;

import com.gesper.server.spent.entity.Spent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpentRepository extends JpaRepository<Spent, Long> {

    Page<Spent> findByUserId(Long userId, Pageable pageable);

    Optional<Spent> findByIdAndUserId(Long id, Long userId);
}
