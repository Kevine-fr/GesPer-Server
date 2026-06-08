package com.gesper.server.gain.service;

import com.gesper.server.categorie.entity.Categorie;
import com.gesper.server.categorie.repository.CategorieRepository;
import com.gesper.server.common.exception.ResourceNotFoundException;
import com.gesper.server.gain.dto.GainDtos.*;
import com.gesper.server.gain.entity.Gain;
import com.gesper.server.gain.repository.GainRepository;
import com.gesper.server.security.SecurityUtils;
import com.gesper.server.user.entity.User;
import com.gesper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GainService {

    private final GainRepository repository;
    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;
    private final GainMapper mapper;

    @Transactional(readOnly = true)
    public Page<GainResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<GainResponse> findAllByCurrentUser(Pageable pageable) {
        return repository.findByUserId(SecurityUtils.getCurrentUserId(), pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<GainResponse> findAllByUserId(Long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public GainResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public GainResponse findByIdForCurrentUser(Long id) {
        Gain g = repository.findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Gain introuvable !"));
        return mapper.toResponse(g);
    }

    @Transactional
    public GainResponse create(CreateGainRequest request) {
        User user = userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));
        Gain gain = Gain.builder()
                .user(user)
                .categorie(cat)
                .libelle(request.libelle())
                .sum(request.sum())
                .recurrent(Boolean.TRUE.equals(request.isReccurent()))
                .deleted(false)
                .build();
        return mapper.toResponse(repository.save(gain));
    }

    @Transactional
    public GainResponse createByAdmin(CreateGainByAdminRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable !"));
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));
        Gain gain = Gain.builder()
                .user(user)
                .categorie(cat)
                .libelle(request.libelle())
                .sum(request.sum())
                .recurrent(Boolean.TRUE.equals(request.isReccurent()))
                .deleted(false)
                .build();
        return mapper.toResponse(repository.save(gain));
    }

    @Transactional
    public GainResponse update(Long id, UpdateGainRequest request) {
        Gain g = getOrThrow(id);
        applyUpdate(g, request);
        return mapper.toResponse(repository.save(g));
    }

    @Transactional
    public GainResponse updateByCurrentUser(Long id, UpdateGainRequest request) {
        Gain g = repository.findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Gain introuvable !"));
        applyUpdate(g, request);
        return mapper.toResponse(repository.save(g));
    }

    @Transactional
    public void softDelete(Long id) {
        Gain g = repository.findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Gain introuvable !"));
        repository.delete(g);
    }

    @Transactional
    public void hardDelete(Long id) {
        Gain g = getOrThrow(id);
        repository.delete(g);
    }

    private Gain getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gain introuvable !"));
    }

    private void applyUpdate(Gain g, UpdateGainRequest request) {
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));
        g.setCategorie(cat);
        g.setLibelle(request.libelle());
        g.setSum(request.sum());
        g.setRecurrent(Boolean.TRUE.equals(request.isReccurent()));
    }
}
