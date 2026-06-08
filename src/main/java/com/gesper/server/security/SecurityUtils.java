package com.gesper.server.security;

import com.gesper.server.common.exception.UnauthorizedException;
import com.gesper.server.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new UnauthorizedException("Utilisateur non authentifié.");
        }
        return details.getUser();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static boolean isAdmin() {
        try {
            return "ROLE_ADMIN".equals(getCurrentUser().getRole().getName().name());
        } catch (Exception ex) {
            return false;
        }
    }
}
