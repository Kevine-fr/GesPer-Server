package com.gesper.server.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le nom est requis")
        @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères")
        String name,

        @NotBlank(message = "L'email est requis")
        @Email(message = "Email invalide")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Le mot de passe est requis")
        @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
        String password
) {}
