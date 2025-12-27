package org.example.blogbackend.post.model.projection;

import java.time.Instant;
import java.util.UUID;

public record DraftCardProjection(
        UUID id,
        UUID postId,
        String title,
        String imageUrl,
        String description,
        Instant updatedAt,
        Instant createdAt
) {}
