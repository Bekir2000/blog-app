package org.example.blogbackend.auth.model.dto.request;

public record RefreshRequest(
        String accessToken,
        String refreshToken
) {
}
