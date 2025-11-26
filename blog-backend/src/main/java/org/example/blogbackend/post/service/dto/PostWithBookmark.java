package org.example.blogbackend.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.blogbackend.post.model.entity.Post;

public record PostWithBookmark(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Post post,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isBookmarked
) {
}
