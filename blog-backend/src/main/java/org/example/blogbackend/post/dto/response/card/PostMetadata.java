package org.example.blogbackend.post.dto.response.card;

public record PostMetadata(
        long likeCount,
        long commentCount,
        int readingTimeMinutes,
        boolean isLiked,
        boolean isBookmarked
) {}
