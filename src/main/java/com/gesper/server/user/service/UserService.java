package com.gesper.server.user.service;

import com.gesper.server.auth.service.RefreshTokenService;
import com.gesper.server.common.exception.ConflictException;
import com.gesper.server.common.exception.ResourceNotFoundException;
import com.gesper.server.security.SecurityUtils;
import com.gesper.server.user.dto.UpdateUserRequest;
import com.gesper.server.user.dto.UserMapper;
import com.gesper.server.user.dto.UserResponse;
import com.gesper.server.user.entity.User;
import com.gesper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAllWithDetails(pageable).map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'existe pas !"));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Long id = SecurityUtils.getCurrentUserId();
        return findById(id);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'existe pas !"));
        applyUpdate(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateCurrent(UpdateUserRequest request) {
        return update(SecurityUtils.getCurrentUserId(), request);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'existe pas !"));
        refreshTokenService.revokeAll(user);
        userRepository.delete(user);
    }

    @Transactional
    public void disable(Long id) {
        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'existe pas !"));
        if (user.getClient() == null) {
            throw new ResourceNotFoundException("Ce client n'existe pas !");
        }
        if (!user.getClient().isActif()) {
            throw new ConflictException("Ce client est déjà banni !");
        }
        user.getClient().setActif(false);
        refreshTokenService.revokeAll(user);
        userRepository.save(user);
    }

    @Transactional
    public void enable(Long id) {
        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'existe pas !"));
        if (user.getClient() == null) {
            throw new ResourceNotFoundException("Ce client n'existe pas !");
        }
        if (user.getClient().isActif()) {
            throw new ConflictException("Ce client est déjà actif !");
        }
        user.getClient().setActif(true);
        userRepository.save(user);
    }

    private void applyUpdate(User user, UpdateUserRequest request) {
        if (StringUtils.hasText(request.name())) {
            user.setName(request.name());
        }
        if (StringUtils.hasText(request.email()) && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Cet email est déjà utilisé.");
            }
            user.setEmail(request.email());
        }
        if (StringUtils.hasText(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
    }
}
