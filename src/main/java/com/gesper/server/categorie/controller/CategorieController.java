package com.gesper.server.categorie.controller;

import com.gesper.server.categorie.dto.CategorieDtos.CategorieRequest;
import com.gesper.server.categorie.dto.CategorieDtos.CategorieResponse;
import com.gesper.server.categorie.service.CategorieService;
import com.gesper.server.common.dto.ApiResponse;
import com.gesper.server.common.dto.PageResponse;
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

@Tag(name = "Catégories", description = "CRUD des catégories de dépenses / gains")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategorieController {

    private final CategorieService service;

    @Operation(summary = "Liste paginée des catégories")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategorieResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Récupération des catégories avec succès !",
                PageResponse.from(service.findAll(pageable))));
    }

    @Operation(summary = "Récupère une catégorie")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategorieResponse>> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Récupération de la catégorie avec succès !",
                service.findById(id)));
    }

    @Operation(summary = "[ADMIN] Crée une catégorie")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategorieResponse>> create(@Valid @RequestBody CategorieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catégorie créée avec succès !", service.create(request)));
    }

    @Operation(summary = "[ADMIN] Met à jour une catégorie")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategorieResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody CategorieRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Catégorie modifiée avec succès !",
                service.update(id, request)));
    }

    @Operation(summary = "[ADMIN] Supprime une catégorie")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Catégorie supprimée avec succès !"));
    }
}
