package com.gesper.server.security.oauth2;

import com.gesper.server.user.entity.AuthProvider;

import java.util.Map;

/**
 * Extrait les informations utilisateur depuis les attributs OAuth2 selon le fournisseur.
 */
public class OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final AuthProvider provider;

    public OAuth2UserInfo(AuthProvider provider, Map<String, Object> attributes) {
        this.provider = provider;
        this.attributes = attributes;
    }

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return new OAuth2UserInfo(AuthProvider.GOOGLE, attributes);
        }
        throw new IllegalArgumentException("Provider OAuth2 non supporté : " + registrationId);
    }

    public AuthProvider getProvider() { return provider; }
    public Map<String, Object> getAttributes() { return attributes; }

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
