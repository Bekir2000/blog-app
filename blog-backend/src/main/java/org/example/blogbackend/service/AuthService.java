package org.example.blogbackend.service;

import org.example.blogbackend.model.dto.requests.LoginRequest;
import org.example.blogbackend.model.dto.requests.RegisterRequest;
import org.example.blogbackend.security.jwt.JwtParsed;

public interface AuthService {

    public record TokenPair(
            JwtParsed accessToken,
            JwtParsed refreshToken
    ) {}


    public TokenPair register(RegisterRequest req);
    public TokenPair login(LoginRequest loginRequest);
    public void logout(String refreshToken);
    public TokenPair refreshBoth(String refreshToken);
    public TokenPair refreshAccess(String refreshToken);
}
