package com.gesper.server.spent.controller;

import com.gesper.server.common.dto.ApiResponse;
import com.gesper.server.common.dto.PageResponse;
import com.gesper.server.spent.dto.SpentDtos.*;
import com.gesper.server.spent.service.SpentService;
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

@Tag(name = "Dépenses", description = "Gestion des dépenses")
@RestController
@RequestMapping("/spents")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SpentController {

    private final SpentService service;

    // ---------- Endpoints utilisateur ----------

    @Operation(summary = "Liste paginée des dépenses de l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<SpentResponse>>> findAllByCurrentUser(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des dépenses avec succès !",
                PageResponse.from(service.findAllByCurrentUser(pageable))));
    }

    @Operation(summary = "Récupère une dépense de l'utilisateur connecté")
    @GetMapping("/me/{spentId}")
    public ResponseEntity<ApiResponse<SpentResponse>> findOneByCurrentUser(@PathVariable Long spentId) {
        return ResponseEntity.ok(ApiResponse.success("Récupération de la dépense avec succès !",
                service.findByIdForCurrentUser(spentId)));
    }

    @Operation(summary = "Crée une dépense pour l'utilisateur connecté")
    @PostMapping
    public ResponseEntity<ApiResponse<SpentResponse>> create(@Valid @RequestBody CreateSpentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dépense créée avec succès !", service.create(request)));
    }

    @Operation(summary = "Modifie une dépense de l'utilisateur connecté")
    @PutMapping("/me/{spentId}")
    public ResponseEntity<ApiResponse<SpentResponse>> updateByCurrentUser(
            @PathVariable Long spentId, @Valid @RequestBody UpdateSpentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Dépense modifiée avec succès !",
                service.updateByCurrentUser(spentId, request)));
    }

    @Operation(summary = "Soft-delete d'une dépense de l'utilisateur connecté")
    @PatchMapping("/me/{spentId}/soft-delete")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable Long spentId) {
        service.softDelete(spentId);
        return ResponseEntity.ok(ApiResponse.success("Dépense supprimée avec succès !"));
    }

    // ---------- Endpoints admin ----------

    @Operation(summary = "[ADMIN] Liste paginée de toutes les dépenses")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<SpentResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des dépenses avec succès !",
                PageResponse.from(service.findAll(pageable))));
    }

    @Operation(summary = "[ADMIN] Liste paginée des dépenses d'un utilisateur")
    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<SpentResponse>>> findAllByUser(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des dépenses avec succès !",
                PageResponse.from(service.findAllByUserId(userId, pageable))));
    }

    @Operation(summary = "[ADMIN] Récupère une dépense par id")
    @GetMapping("/admin/{spentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpentResponse>> findOne(@PathVariable Long spentId) {
        return ResponseEntity.ok(ApiResponse.success("Récupération de la dépense avec succès !",
                service.findById(spentId)));
    }

    @Operation(summary = "[ADMIN] Crée une dépense pour un utilisateur")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpentResponse>> createByAdmin(@Valid @RequestBody CreateSpentByAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dépense créée avec succès !", service.createByAdmin(request)));
    }

    @Operation(summary = "[ADMIN] Modifie une dépense")
    @PutMapping("/admin/{spentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpentResponse>> update(
            @PathVariable Long spentId, @Valid @RequestBody UpdateSpentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Dépense modifiée avec succès !",
                service.update(spentId, request)));
    }

    @Operation(summary = "[ADMIN] Supprime une dépense")
    @DeleteMapping("/admin/{spentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long spentId) {
        service.hardDelete(spentId);
        return ResponseEntity.ok(ApiResponse.success("Dépense supprimée avec succès !"));
    }
}
