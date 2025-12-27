package org.example.blogbackend.post.dto.response.card;

import org.example.blogbackend.post.dto.response.AuthorSummary;
import org.example.blogbackend.post.model.Category;

import java.time.Instant;
import java.util.UUID;

public record PostCardResponse(
        UUID id,
        String title,
        String description,
        String imageUrl,

        AuthorSummary author,
        PostMetadata meta,

        Category category,
        String[] tags,

        Instant createdAt,
        Instant updatedAt
){}
