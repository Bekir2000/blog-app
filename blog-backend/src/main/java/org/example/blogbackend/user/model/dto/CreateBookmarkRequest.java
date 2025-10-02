package org.example.blogbackend.user.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBookmarkRequest(
        @NotNull
        UUID postId
) {
}
