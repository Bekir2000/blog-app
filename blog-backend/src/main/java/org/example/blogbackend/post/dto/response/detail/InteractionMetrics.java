package org.example.blogbackend.post.dto.response.detail;

public record InteractionMetrics(
        int likeCount,
        int commentCount,
        boolean liked,
        boolean bookmarked
) {}
