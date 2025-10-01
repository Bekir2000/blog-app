package org.example.blogbackend.post.controller.dto.response;

import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.category.model.dto.response.CategoryResponse;
import org.example.blogbackend.tag.model.dto.response.TagResponse;
import org.example.blogbackend.post.model.PostStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String title,
        String content,
        String description,
        String imageUrl,
        UserResponse author,
        CategoryResponse category,
        Set<TagResponse> tags,
        Integer views,
        Integer likes,
        Integer commentsCount,
        Integer readingTime,
        PostStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
