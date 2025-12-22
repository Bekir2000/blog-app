package org.example.blogbackend.post.dto.response.detail;

public record InteractionMetrics(
        boolean liked,
        boolean bookmarked
) {}
