package org.example.blogbackend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.auth.service.AuthService;
import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.mapper.UserMapper;
import org.example.blogbackend.auth.model.dto.request.LoginRequest;
import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.auth.model.dto.response.AuthResponse;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.common.security.jwt.JwtParsed;
import org.example.blogbackend.common.security.jwt.RefreshCookieService;
import org.example.blogbackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RefreshCookieService refreshCookieService;
    private final AuthService authService;

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse res) {
        AuthService.TokenPair pair = authService.register(registerRequest);
        return respondWithTokens(res, pair);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse res) {
        AuthService.TokenPair pair = authService.login(loginRequest.email(), loginRequest.password());
        return respondWithTokens(res, pair);
    }

    @PostMapping("/refresh-both")
    public ResponseEntity<AuthResponse> refreshBoth(HttpServletRequest req, HttpServletResponse res) {
        String refreshTokenValue = refreshCookieService.read(req);
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var pair = authService.refreshBoth(refreshTokenValue);
        return respondWithTokens(res, pair); // sets new cookie + returns access
    }

    @PostMapping("/refresh-access")
    public ResponseEntity<AuthResponse> refreshAccess(HttpServletRequest req, HttpServletResponse res) {
        String refreshTokenValue = refreshCookieService.read(req);
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var pair = authService.refreshAccess(refreshTokenValue);
        return respondWithTokens(res, pair); // sets new cookie + returns access
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        String refreshTokenValue = refreshCookieService.read(req);
        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            authService.logout(refreshTokenValue); // blacklist refresh jti
        }
        refreshCookieService.clear(res); // delete cookie client-side
        return ResponseEntity.noContent().build();
    }
    /* -------------------- private helpers -------------------- */

    private ResponseEntity<AuthResponse> respondWithTokens(
            HttpServletResponse res,
            AuthService.TokenPair pair
    ) {
        JwtParsed access  = pair.accessToken();
        JwtParsed refresh = pair.refreshToken();

        refreshCookieService.attach(res, refresh.getValue(), refresh.getExpiresAt());

        // body with access token info
        AuthResponse body = AuthResponse.builder()
                .token(access.getValue())
                .expiresIn(access.getExpiresAt().getEpochSecond())
                .build();

        return ResponseEntity.ok(body);
    }
}
