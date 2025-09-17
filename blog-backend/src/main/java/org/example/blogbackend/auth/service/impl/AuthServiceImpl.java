package org.example.blogbackend.auth.service.impl;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.auth.model.entity.BlacklistedRefreshToken;
import org.example.blogbackend.auth.repository.BlacklistedRefreshTokenRepository;
import org.example.blogbackend.auth.model.dto.request.LoginRequest;
import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.common.security.jwt.JwtParsed;
import org.example.blogbackend.common.security.jwt.JwtTokenType;
import org.example.blogbackend.common.security.jwt.JwtUtil;
import org.example.blogbackend.auth.service.AuthService;
import org.example.blogbackend.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final BlacklistedRefreshTokenRepository blacklistedRepo;
    private final JwtUtil jwt;

    /* ======== public API ======== */

    @Override
    @Transactional
    public TokenPair register(RegisterRequest req) {
        userService.register(req);
        LoginRequest loginReq = new LoginRequest(req.email(), req.password());
        return login(loginReq.email(), loginReq.password());
    }

    @Override
    public AuthService.TokenPair login(String email, String password) {
        BlogUserDetails u = authenticate(email, password);
        JwtParsed access  = jwt.generateAccessToken(u.getUserId());
        JwtParsed refresh = jwt.generateRefreshToken(u.getUserId());
        return new AuthService.TokenPair(access, refresh);
    }

    @Override
    @Transactional
    public AuthService.TokenPair refreshBoth(String refreshTokenValue) {
        JwtParsed r0 = parseAndValidateRefresh(refreshTokenValue);
        JwtParsed a1 = jwt.generateAccessToken(r0.getUserId());
        JwtParsed r1 = jwt.generateRefreshToken(r0.getUserId());
        blacklist(r0); // replay protection
        return new AuthService.TokenPair(a1, r1);
    }

    @Override
    public AuthService.TokenPair refreshAccess(String refreshTokenValue) {
        JwtParsed r = parseAndValidateRefresh(refreshTokenValue);
        JwtParsed a = jwt.generateAccessToken(r.getUserId());
        return new AuthService.TokenPair(a, r); // return same refresh
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        JwtParsed r = parseAndValidateRefresh(refreshTokenValue);
        // idempotent
        if (!blacklistedRepo.existsByJti(r.getJti())) {
            blacklist(r);
        }
    }

    /* ======== helpers ======== */

    private BlogUserDetails authenticate(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return (BlogUserDetails) auth.getPrincipal(); // principal IS your BlogUserDetails
    }

    /** parse + basic validations shared by refresh/logout */
    private JwtParsed parseAndValidateRefresh(String refreshTokenValue) {
        if (!jwt.validateToken(refreshTokenValue)) throw new JwtException("Invalid refresh token");

        JwtParsed r = jwt.parse(refreshTokenValue);

        if (r.getType() != JwtTokenType.REFRESH) throw new JwtException("Only refresh tokens allowed");
        if (r.getExpiresAt().isBefore(Instant.now())) throw new JwtException("Refresh token expired");
        if (blacklistedRepo.existsByJti(r.getJti())) throw new JwtException("Refresh token blacklisted");

        return r;
    }

    private void blacklist(JwtParsed r) {
        blacklistedRepo.save(
                BlacklistedRefreshToken.builder()
                        .jti(r.getJti())
                        .expiresAt(r.getExpiresAt())
                        .createdAt(Instant.now())
                        .build()
        );
    }
}

