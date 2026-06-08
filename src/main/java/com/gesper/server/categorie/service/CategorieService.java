package com.gesper.server.categorie.service;

import com.gesper.server.categorie.dto.CategorieDtos.CategorieMapper;
import com.gesper.server.categorie.dto.CategorieDtos.CategorieRequest;
import com.gesper.server.categorie.dto.CategorieDtos.CategorieResponse;
import com.gesper.server.categorie.entity.Categorie;
import com.gesper.server.categorie.repository.CategorieRepository;
import com.gesper.server.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository repository;
    private final CategorieMapper mapper;

    @Transactional(readOnly = true)
    public Page<CategorieResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CategorieResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public CategorieResponse create(CategorieRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public CategorieResponse update(Long id, CategorieRequest request) {
        Categorie c = getOrThrow(id);
        mapper.update(c, request);
        return mapper.toResponse(repository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Categorie getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));
    }
}
