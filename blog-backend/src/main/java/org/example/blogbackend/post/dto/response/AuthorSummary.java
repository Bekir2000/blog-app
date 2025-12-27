package org.example.blogbackend.post.dto.response;

import java.util.UUID;

public record AuthorSummary(
        UUID id,
        String firstName,
        String lastName,
        String imageUrl,
        boolean isFollowed
) {}
