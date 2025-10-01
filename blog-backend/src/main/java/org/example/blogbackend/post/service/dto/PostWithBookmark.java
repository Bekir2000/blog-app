package org.example.blogbackend.post.service.dto;

import org.example.blogbackend.post.model.entity.Post;

public record PostWithBookmark(
        Post post,
        boolean isBookmarked
) {
}
