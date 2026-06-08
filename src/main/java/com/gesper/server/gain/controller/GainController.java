package com.gesper.server.gain.controller;

import com.gesper.server.common.dto.ApiResponse;
import com.gesper.server.common.dto.PageResponse;
import com.gesper.server.gain.dto.GainDtos.*;
import com.gesper.server.gain.service.GainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gains", description = "Gestion des revenus")
@RestController
@RequestMapping("/gains")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GainController {

    private final GainService service;

    // ---------- Endpoints utilisateur ----------

    @Operation(summary = "Liste paginée des gains de l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<GainResponse>>> findAllByCurrentUser(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des gains avec succès !",
                PageResponse.from(service.findAllByCurrentUser(pageable))));
    }

    @Operation(summary = "Récupère un gain de l'utilisateur connecté")
    @GetMapping("/me/{gainId}")
    public ResponseEntity<ApiResponse<GainResponse>> findOneByCurrentUser(@PathVariable Long gainId) {
        return ResponseEntity.ok(ApiResponse.success("Récupération du gain avec succès !",
                service.findByIdForCurrentUser(gainId)));
    }

    @Operation(summary = "Crée un gain pour l'utilisateur connecté")
    @PostMapping
    public ResponseEntity<ApiResponse<GainResponse>> create(@Valid @RequestBody CreateGainRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Gain créé avec succès !", service.create(request)));
    }

    @Operation(summary = "Modifie un gain de l'utilisateur connecté")
    @PutMapping("/me/{gainId}")
    public ResponseEntity<ApiResponse<GainResponse>> updateByCurrentUser(
            @PathVariable Long gainId, @Valid @RequestBody UpdateGainRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Gain modifié avec succès !",
                service.updateByCurrentUser(gainId, request)));
    }

    @Operation(summary = "Soft-delete d'un gain de l'utilisateur connecté")
    @PatchMapping("/me/{gainId}/soft-delete")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable Long gainId) {
        service.softDelete(gainId);
        return ResponseEntity.ok(ApiResponse.success("Gain supprimé avec succès !"));
    }

    // ---------- Endpoints admin ----------

    @Operation(summary = "[ADMIN] Liste paginée de tous les gains")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<GainResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des gains avec succès !",
                PageResponse.from(service.findAll(pageable))));
    }

    @Operation(summary = "[ADMIN] Liste paginée des gains d'un utilisateur")
    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<GainResponse>>> findAllByUser(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des gains avec succès !",
                PageResponse.from(service.findAllByUserId(userId, pageable))));
    }

    @Operation(summary = "[ADMIN] Récupère un gain par id")
    @GetMapping("/admin/{gainId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GainResponse>> findOne(@PathVariable Long gainId) {
        return ResponseEntity.ok(ApiResponse.success("Récupération du gain avec succès !",
                service.findById(gainId)));
    }

    @Operation(summary = "[ADMIN] Crée un gain pour un utilisateur")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GainResponse>> createByAdmin(@Valid @RequestBody CreateGainByAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Gain créé avec succès !", service.createByAdmin(request)));
    }

    @Operation(summary = "[ADMIN] Modifie un gain")
    @PutMapping("/admin/{gainId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GainResponse>> update(
            @PathVariable Long gainId, @Valid @RequestBody UpdateGainRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Gain modifié avec succès !", service.update(gainId, request)));
    }

    @Operation(summary = "[ADMIN] Supprime un gain")
    @DeleteMapping("/admin/{gainId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long gainId) {
        service.hardDelete(gainId);
        return ResponseEntity.ok(ApiResponse.success("Gain supprimé avec succès !"));
    }
}
