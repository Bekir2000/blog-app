package org.example.blogbackend.auth.model.dto.request;

public record LoginRequest(
    String email,
    String password
) {}
