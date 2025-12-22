package org.example.blogbackend.post.dto.response.card.draft;

import java.time.Instant;
import java.util.UUID;

public record DraftCardResponse(
        UUID id,
        String title,
        String description,
        String imageUrl,

        Instant createdAt,
        Instant updatedAt
) {
}
