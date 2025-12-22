package org.example.blogbackend.user.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,

        String profileImageUrl,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isOnline,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant lastActiveAt
) {}
