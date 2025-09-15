package org.example.blogbackend.model.dto.requests;

public record RegisterRequest(
    String name,
    String email,
    String password,
    String profileImageUrl
) {}
