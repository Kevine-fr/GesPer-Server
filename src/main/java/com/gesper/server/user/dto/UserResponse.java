package com.gesper.server.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gesper.server.user.entity.AuthProvider;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        String profilePictureUrl,
        AuthProvider authProvider,
        boolean enabled,
        Boolean clientActif,
        Instant emailVerifiedAt,
        Instant createdAt,
        Instant updatedAt
) {}
