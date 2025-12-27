package org.example.blogbackend.post.dto.response.detail.draft;

import org.example.blogbackend.post.model.Category;

import java.util.UUID;

public record DraftDetailResponse(
        UUID id,
        UUID postId,
        String title,
        String content,
        String imageUrl,
        Category category,
        String[] tags
) {
}
