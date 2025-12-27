package org.example.blogbackend.post.dto.response.detail;

import org.example.blogbackend.post.dto.response.AuthorSummary;
import org.example.blogbackend.post.model.Category;

import java.time.Instant;
import java.util.UUID;

// 1. Main Response
public record PostDetailResponse(
        UUID id,
        String title,
        String content,
        String description,
        String imageUrl,

        int readingTime,

        Category category,
        String[] tags,

        InteractionMetrics metrics,
        AuthorSummary author,

        Instant createdAt
) {}
