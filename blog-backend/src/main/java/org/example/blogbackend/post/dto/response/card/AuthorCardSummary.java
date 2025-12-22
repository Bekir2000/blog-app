package org.example.blogbackend.post.dto.response.card;

public record AuthorCardSummary(
        String firstName,
        String lastName,
        String imageUrl
) {}
