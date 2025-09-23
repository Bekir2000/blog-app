package org.example.blogbackend.auth.model.dto.request;

public record RegisterRequest(
    String username,
    String firstName,
    String lastName,
    String email,
    String password,
    String profileImageUrl
) {}
