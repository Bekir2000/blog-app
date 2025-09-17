package org.example.blogbackend.auth.service;

import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.common.security.jwt.JwtParsed;

public interface AuthService {

    record TokenPair(
            JwtParsed accessToken,
            JwtParsed refreshToken
    ) {}


    TokenPair register(RegisterRequest req);
    TokenPair login(String email, String password);
    void logout(String refreshToken);
    TokenPair refreshBoth(String refreshToken);
    TokenPair refreshAccess(String refreshToken);
}
