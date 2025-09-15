package org.example.blogbackend.model.dto.requests;

public record LoginRequest(
    String email,
    String password
) {}
