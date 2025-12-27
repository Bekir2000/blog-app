package org.example.blogbackend.post.dto.response.card.draft;

import org.example.blogbackend.post.model.Category;

import java.time.Instant;
import java.util.UUID;

public record DraftCardResponse(
        UUID id,
        UUID postId,
        String title,
        String description,
        String imageUrl,

        Category category,
        String[] tags,

        Instant createdAt,
        Instant updatedAt
) {
}
