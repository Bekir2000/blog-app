package org.example.blogbackend.user.model.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String profileImageUrl
) {}
