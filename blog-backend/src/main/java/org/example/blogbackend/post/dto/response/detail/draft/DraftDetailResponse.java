package org.example.blogbackend.post.dto.response.detail.draft;

import java.util.UUID;

public record DraftDetailResponse(
        String title,
        String content,
        String imageUrl
) {
}
