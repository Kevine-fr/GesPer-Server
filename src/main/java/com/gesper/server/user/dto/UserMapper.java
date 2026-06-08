package com.gesper.server.user.dto;

import com.gesper.server.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName().name() : null,
                user.getProfilePictureUrl(),
                user.getAuthProvider(),
                user.isEnabled(),
                user.getClient() != null ? user.getClient().isActif() : null,
                user.getEmailVerifiedAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
