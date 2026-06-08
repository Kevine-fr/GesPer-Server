package com.gesper.server.spent.service;

import com.gesper.server.categorie.entity.Categorie;
import com.gesper.server.categorie.repository.CategorieRepository;
import com.gesper.server.common.exception.ResourceNotFoundException;
import com.gesper.server.gain.entity.Gain;
import com.gesper.server.gain.repository.GainRepository;
import com.gesper.server.security.SecurityUtils;
import com.gesper.server.spent.dto.SpentDtos.*;
import com.gesper.server.spent.entity.Spent;
import com.gesper.server.spent.repository.SpentRepository;
import com.gesper.server.user.entity.User;
import com.gesper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpentService {

    private final SpentRepository repository;
    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;
    private final GainRepository gainRepository;
    private final SpentMapper mapper;

    @Transactional(readOnly = true)
    public Page<SpentResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SpentResponse> findAllByCurrentUser(Pageable pageable) {
        return repository.findByUserId(SecurityUtils.getCurrentUserId(), pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SpentResponse> findAllByUserId(Long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SpentResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public SpentResponse findByIdForCurrentUser(Long id) {
        return mapper.toResponse(
                repository.findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable !"))
        );
    }

    @Transactional
    public SpentResponse create(CreateSpentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));

        Gain gain = null;
        if (request.gainId() != null) {
            gain = gainRepository.findByIdAndUserId(request.gainId(), userId).orElse(null);
        }

        Spent spent = Spent.builder()
                .user(user)
                .gain(gain)
                .categorie(cat)
                .libelle(request.libelle())
                .value(request.value())
                .spent(Boolean.TRUE.equals(request.isSpent()))
                .deleted(false)
                .build();
        return mapper.toResponse(repository.save(spent));
    }

    @Transactional
    public SpentResponse createByAdmin(CreateSpentByAdminRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable !"));
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));
        Gain gain = request.gainId() != null
                ? gainRepository.findById(request.gainId()).orElse(null) : null;

        Spent spent = Spent.builder()
                .user(user)
                .gain(gain)
                .categorie(cat)
                .libelle(request.libelle())
                .value(request.value())
                .spent(Boolean.TRUE.equals(request.isSpent()))
                .deleted(false)
                .build();
        return mapper.toResponse(repository.save(spent));
    }

    @Transactional
    public SpentResponse update(Long id, UpdateSpentRequest request) {
        Spent s = getOrThrow(id);
        applyUpdate(s, request, null);
        return mapper.toResponse(repository.save(s));
    }

    @Transactional
    public SpentResponse updateByCurrentUser(Long id, UpdateSpentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Spent s = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable !"));
        applyUpdate(s, request, userId);
        return mapper.toResponse(repository.save(s));
    }

    @Transactional
    public void softDelete(Long id) {
        Spent s = repository.findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable !"));
        repository.delete(s);
    }

    @Transactional
    public void hardDelete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Spent getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable !"));
    }

    private void applyUpdate(Spent s, UpdateSpentRequest request, Long userIdScope) {
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable !"));
        s.setCategorie(cat);
        if (request.gainId() != null) {
            Gain g = (userIdScope != null
                    ? gainRepository.findByIdAndUserId(request.gainId(), userIdScope)
                    : gainRepository.findById(request.gainId())).orElse(null);
            s.setGain(g);
        } else {
            s.setGain(null);
        }
        s.setLibelle(request.libelle());
        s.setValue(request.value());
        s.setSpent(Boolean.TRUE.equals(request.isSpent()));
    }
}
