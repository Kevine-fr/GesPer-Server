package com.gesper.server.security.oauth2;

import com.gesper.server.auth.service.RefreshTokenService;
import com.gesper.server.security.CustomUserDetails;
import com.gesper.server.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * À l'issue d'une connexion OAuth2 réussie, génère JWT + refresh et redirige vers l'URI front configurée.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${gesper.security.oauth2.authorized-redirect-uris}")
    private String authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, authentication);
        if (response.isCommitted()) {
            log.debug("Réponse déjà committée, redirection vers {} impossible.", targetUrl);
            return;
        }
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null && !isAuthorizedRedirectUri(redirectUri)) {
            throw new IllegalArgumentException("URI de redirection non autorisée : " + redirectUri);
        }
        String target = redirectUri != null ? redirectUri : defaultRedirectUri();

        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(details.getUser());
        String refreshToken = refreshTokenService.create(details.getUser()).getToken();

        return UriComponentsBuilder.fromUriString(target)
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    private String defaultRedirectUri() {
        return authorizedRedirectUris.split(",")[0].trim();
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        List<String> authorized = Arrays.stream(authorizedRedirectUris.split(",")).map(String::trim).toList();
        return authorized.stream().anyMatch(authorizedUri -> {
            URI au = URI.create(authorizedUri);
            return au.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                    && au.getPort() == clientRedirectUri.getPort();
        });
    }
}
