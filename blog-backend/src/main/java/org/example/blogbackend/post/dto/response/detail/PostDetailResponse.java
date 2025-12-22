package org.example.blogbackend.post.dto.response.detail;

import org.example.blogbackend.post.model.Category;

import java.util.UUID;

// 1. Main Response
public record PostDetailResponse(
        UUID postId,
        String title,
        String content,
        String description,
        String imageUrl,
        Category category,
        String[] tags,
        InteractionMetrics metrics,
        AuthorDetailSummary author
) {}
