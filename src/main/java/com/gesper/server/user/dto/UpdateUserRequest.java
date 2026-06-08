package com.gesper.server.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 255) String name,
        @Email @Size(max = 255) String email,
        @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères") String password
) {}
