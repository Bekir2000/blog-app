package org.example.blogbackend.post.dto.response.detail;

public record AuthorDetailSummary(
        String firstName,
        String lastName,
        boolean isFollowed
) {}
