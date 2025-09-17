package org.example.blogbackend.auth.model.dto.request;

public record RegisterRequest(
    String name,
    String email,
    String password,
    String profileImageUrl
) {}
