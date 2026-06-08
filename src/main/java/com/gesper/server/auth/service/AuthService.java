package com.gesper.server.auth.service;

import com.gesper.server.auth.dto.*;
import com.gesper.server.auth.entity.RefreshToken;
import com.gesper.server.auth.entity.VerificationCode.CodePurpose;
import com.gesper.server.common.exception.ConflictException;
import com.gesper.server.common.exception.ForbiddenException;
import com.gesper.server.common.exception.UnauthorizedException;
import com.gesper.server.mail.MailService;
import com.gesper.server.auth.repository.RefreshTokenRepository;
import com.gesper.server.security.JwtService;
import com.gesper.server.security.SecurityUtils;
import com.gesper.server.user.dto.UserMapper;
import com.gesper.server.user.entity.AuthProvider;
import com.gesper.server.user.entity.Client;
import com.gesper.server.user.entity.ERole;
import com.gesper.server.user.entity.User;
import com.gesper.server.user.repository.ClientRepository;
import com.gesper.server.user.repository.RoleRepository;
import com.gesper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationCodeService verificationCodeService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final UserMapper userMapper;

    @Value("${gesper.mail.admin-recipient}")
    private String adminRecipient;

    /**
     * Inscription d'un admin. Doit fournir un code de vérification valide reçu par l'admin email global.
     */
    @Transactional
    public void registerAdmin(RegisterRequest request, String verificationCode) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Cet email est déjà utilisé.");
        }
        verificationCodeService.verify(request.email(), verificationCode, CodePurpose.REGISTER_ADMIN);

        var role = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_ADMIN introuvable."));

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .authProvider(AuthProvider.LOCAL)
                .enabled(true)
                .build();
        userRepository.save(user);
        log.info("Admin créé : {}", user.getEmail());
    }

    /**
     * Inscription d'un client classique avec code de vérification email.
     */
    @Transactional
    public void registerClient(RegisterRequest request, String verificationCode) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Cet email est déjà utilisé.");
        }
        verificationCodeService.verify(request.email(), verificationCode, CodePurpose.REGISTER_CLIENT);

        var role = roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_CLIENT introuvable."));

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .authProvider(AuthProvider.LOCAL)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        Client client = Client.builder().user(user).actif(true).build();
        clientRepository.save(client);
        log.info("Client créé : {}", user.getEmail());
    }

    /**
     * Envoie un code de vérification (admin = à l'admin global, client = au demandeur).
     */
    @Transactional
    public void sendAdminVerificationCode(SendCodeRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Cet email est déjà utilisé.");
        }
        var code = verificationCodeService.generate(request.email(), CodePurpose.REGISTER_ADMIN);
        String date = LocalDateTime.now(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mailService.sendTemplated(
                adminRecipient,
                "Tentative de création d'un compte administrateur — " + request.email(),
                "admin-mail",
                Map.of(
                        "email", request.email(),
                        "date", date,
                        "code", code.getCode()
                )
        );
    }

    @Transactional
    public void sendClientVerificationCode(SendCodeRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Cet email est déjà utilisé.");
        }
        var code = verificationCodeService.generate(request.email(), CodePurpose.REGISTER_CLIENT);
        mailService.sendTemplated(
                request.email(),
                "Confirmation d'inscription — Code de vérification",
                "client-mail",
                Map.of(
                        "email", request.email(),
                        "code", code.getCode()
                )
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmailWithDetails(request.email())
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect !"));

        if (user.getRole().getName() != ERole.ROLE_ADMIN
                && user.getClient() != null && !user.getClient().isActif()) {
            throw new ForbiddenException("Votre compte a été banni !");
        }

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refresh = refreshTokenService.create(user);

        return AuthResponse.of(
                accessToken,
                refresh.getToken(),
                jwtService.getAccessTokenExpirationSeconds(),
                userMapper.toResponse(user)
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        var stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalide."));
        User user = userRepository.findByIdWithDetails(stored.getUser().getId())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable."));

        var newRefresh = refreshTokenService.verifyAndRotate(request.refreshToken(), user);
        String newAccess = jwtService.generateAccessToken(user);
        return AuthResponse.of(
                newAccess,
                newRefresh.getToken(),
                jwtService.getAccessTokenExpirationSeconds(),
                userMapper.toResponse(user)
        );
    }

    @Transactional
    public void logout() {
        try {
            User user = SecurityUtils.getCurrentUser();
            refreshTokenService.revokeAll(user);
        } catch (Exception ex) {
            // Logout doit toujours réussir côté client.
            log.debug("Logout sans authentification active");
        }
    }
}
