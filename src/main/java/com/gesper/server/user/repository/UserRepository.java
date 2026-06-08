package com.gesper.server.user.repository;

import com.gesper.server.user.entity.AuthProvider;
import com.gesper.server.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    Optional<User> findByProviderIdAndAuthProvider(String providerId, AuthProvider authProvider);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.client WHERE u.id = :id")
    Optional<User> findByIdWithDetails(Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.client WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(String email);

    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.client",
            countQuery = "SELECT count(u) FROM User u")
    Page<User> findAllWithDetails(Pageable pageable);
}
