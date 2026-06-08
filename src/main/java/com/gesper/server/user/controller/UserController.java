package com.gesper.server.user.controller;

import com.gesper.server.common.dto.ApiResponse;
import com.gesper.server.common.dto.PageResponse;
import com.gesper.server.user.dto.UpdateUserRequest;
import com.gesper.server.user.dto.UserResponse;
import com.gesper.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Utilisateurs", description = "Gestion des comptes")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Récupère l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        return ResponseEntity.ok(ApiResponse.success("Récupération effectuée avec succès !", userService.getCurrentUser()));
    }

    @Operation(summary = "Met à jour l'utilisateur connecté")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour avec succès !",
                userService.updateCurrent(request)));
    }

    @Operation(summary = "[ADMIN] Liste paginée de tous les utilisateurs")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération effectuée avec succès !",
                PageResponse.from(userService.findAll(pageable))));
    }

    @Operation(summary = "[ADMIN] Récupère un utilisateur par son id")
    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getOne(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Récupération effectuée avec succès !",
                userService.findById(userId)));
    }

    @Operation(summary = "[ADMIN] Met à jour un utilisateur")
    @PutMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long userId,
                                                            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour avec succès !",
                userService.update(userId, request)));
    }

    @Operation(summary = "[ADMIN] Supprime définitivement un utilisateur")
    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.ok(ApiResponse.success("Suppression effectuée avec succès !"));
    }

    @Operation(summary = "[ADMIN] Désactive (bannit) un client")
    @PutMapping("/admin/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disable(@PathVariable Long userId) {
        userService.disable(userId);
        return ResponseEntity.ok(ApiResponse.success("Désactivation effectuée avec succès !"));
    }

    @Operation(summary = "[ADMIN] Réactive un client banni")
    @PutMapping("/admin/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enable(@PathVariable Long userId) {
        userService.enable(userId);
        return ResponseEntity.ok(ApiResponse.success("Activation effectuée avec succès !"));
    }
}
