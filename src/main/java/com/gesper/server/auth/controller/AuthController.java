package com.gesper.server.auth.controller;

import com.gesper.server.auth.dto.*;
import com.gesper.server.auth.service.AuthService;
import com.gesper.server.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentification", description = "Endpoints d'inscription, connexion et gestion des tokens")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Inscription d'un administrateur (code de vérification requis)")
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(
            @Valid @RequestBody RegisterRequest request,
            @RequestParam("code") @NotBlank String code) {
        authService.registerAdmin(request, code);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin créé avec succès !"));
    }

    @Operation(summary = "Inscription d'un client (code de vérification requis)")
    @PostMapping("/register/client")
    public ResponseEntity<ApiResponse<Void>> registerClient(
            @Valid @RequestBody RegisterRequest request,
            @RequestParam("code") @NotBlank String code) {
        authService.registerClient(request, code);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Compte créé avec succès !"));
    }

    @Operation(summary = "Envoie un code de vérification à l'admin global pour création d'un compte admin")
    @PostMapping("/send-code/admin")
    public ResponseEntity<ApiResponse<Void>> sendAdminCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendAdminVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.success("E-mail envoyé avec succès !"));
    }

    @Operation(summary = "Envoie un code de vérification à un client")
    @PostMapping("/send-code/client")
    public ResponseEntity<ApiResponse<Void>> sendClientCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendClientVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.success("E-mail envoyé avec succès !"));
    }

    @Operation(summary = "Connexion par email / mot de passe")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie !", authService.login(request)));
    }

    @Operation(summary = "Rafraîchissement du token via un refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token rafraîchi !", authService.refresh(request)));
    }

    @Operation(summary = "Déconnexion — révoque les refresh tokens")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie !"));
    }
}
