package com.gesper.server.security.oauth2;

import com.gesper.server.common.exception.BadRequestException;
import com.gesper.server.security.CustomUserDetails;
import com.gesper.server.user.entity.Client;
import com.gesper.server.user.entity.ERole;
import com.gesper.server.user.entity.User;
import com.gesper.server.user.repository.ClientRepository;
import com.gesper.server.user.repository.RoleRepository;
import com.gesper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        try {
            return process(request, oAuth2User);
        } catch (Exception ex) {
            log.error("Erreur OAuth2", ex);
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_processing"), ex.getMessage(), ex);
        }
    }

    private OAuth2User process(OAuth2UserRequest request, OAuth2User oAuth2User) {
        String registrationId = request.getClientRegistration().getRegistrationId();
        OAuth2UserInfo info = OAuth2UserInfo.of(registrationId, oAuth2User.getAttributes());

        if (!StringUtils.hasText(info.getEmail())) {
            throw new BadRequestException("Email non disponible chez le fournisseur OAuth2.");
        }

        User user = userRepository.findByEmail(info.getEmail())
                .map(existing -> updateExisting(existing, info))
                .orElseGet(() -> register(info));

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User register(OAuth2UserInfo info) {
        var role = roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_CLIENT manquant — exécutez les migrations."));

        User user = User.builder()
                .name(info.getName() != null ? info.getName() : info.getEmail())
                .email(info.getEmail())
                .authProvider(info.getProvider())
                .providerId(info.getId())
                .profilePictureUrl(info.getImageUrl())
                .emailVerifiedAt(Instant.now())
                .role(role)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        Client client = Client.builder().user(user).actif(true).build();
        clientRepository.save(client);

        log.info("Nouvel utilisateur OAuth2 créé : {}", user.getEmail());
        return user;
    }

    private User updateExisting(User existing, OAuth2UserInfo info) {
        if (existing.getAuthProvider() != info.getProvider()) {
            throw new BadRequestException(
                    "Cet email est déjà associé à " + existing.getAuthProvider() + ". Connectez-vous avec ce fournisseur.");
        }
        existing.setName(info.getName() != null ? info.getName() : existing.getName());
        existing.setProfilePictureUrl(info.getImageUrl());
        return userRepository.save(existing);
    }
}
