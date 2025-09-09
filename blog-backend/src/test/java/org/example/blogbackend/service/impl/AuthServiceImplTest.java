package org.example.blogbackend.service.impl;

import io.jsonwebtoken.JwtException;
import org.example.blogbackend.model.dto.requests.LoginRequest;
import org.example.blogbackend.model.entities.BlacklistedRefreshToken;
import org.example.blogbackend.repository.BlacklistedRefreshTokenRepository;
import org.example.blogbackend.security.BlogUserDetails;
import org.example.blogbackend.security.jwt.JwtParsed;
import org.example.blogbackend.security.jwt.JwtTokenType;
import org.example.blogbackend.security.jwt.JwtUtil;
import org.example.blogbackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private BlacklistedRefreshTokenRepository blacklistedRepo;

    @Mock
    private JwtUtil jwt;

    @InjectMocks
    private AuthServiceImpl authService;

    // Test data
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final UUID testUserId = UUID.randomUUID();
    private final String testAccessToken = "accessToken123";
    private final String testRefreshToken = "refreshToken123";
    private final UUID testJti = UUID.randomUUID();

    @Test
    void login_WithValidCredentials_ShouldReturnTokenPair() {
        // Arrange
        var loginRequest = new LoginRequest(testEmail, testPassword);
        BlogUserDetails userDetails = mock(BlogUserDetails.class);
        JwtParsed accessToken = mock(JwtParsed.class);
        JwtParsed refreshToken = mock(JwtParsed.class);

        when(userDetails.getUserId()).thenReturn(testUserId);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(userDetailsService.loadUserByUsername(testEmail)).thenReturn(userDetails);
        when(jwt.generateAccessToken(testUserId)).thenReturn(accessToken);
        when(jwt.generateRefreshToken(testUserId)).thenReturn(refreshToken);

        // Act
        AuthService.TokenPair result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(testEmail);
        verify(jwt).generateAccessToken(testUserId);
        verify(jwt).generateRefreshToken(testUserId);
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        var loginRequest = new LoginRequest(testEmail, "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService, jwt);
    }

    @Test
    void refreshBoth_WithValidRefreshToken_ShouldReturnNewTokenPair() {
        // Arrange
        JwtParsed oldRefreshToken = mock(JwtParsed.class);
        JwtParsed newAccessToken = mock(JwtParsed.class);
        JwtParsed newRefreshToken = mock(JwtParsed.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(oldRefreshToken);
        when(oldRefreshToken.getType()).thenReturn(JwtTokenType.REFRESH);
        when(oldRefreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(oldRefreshToken.getJti()).thenReturn(testJti);
        when(oldRefreshToken.getUserId()).thenReturn(testUserId);
        when(blacklistedRepo.existsByJti(testJti)).thenReturn(false);
        when(jwt.generateAccessToken(testUserId)).thenReturn(newAccessToken);
        when(jwt.generateRefreshToken(testUserId)).thenReturn(newRefreshToken);

        // Act
        AuthService.TokenPair result = authService.refreshBoth(testRefreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(newAccessToken, result.accessToken());
        assertEquals(newRefreshToken, result.refreshToken());
        verify(jwt).validateToken(testRefreshToken);
        verify(jwt).parse(testRefreshToken);
        verify(blacklistedRepo).existsByJti(testJti);
        verify(jwt).generateAccessToken(testUserId);
        verify(jwt).generateRefreshToken(testUserId);
        verify(blacklistedRepo).save(any(BlacklistedRefreshToken.class));
    }

    @Test
    void refreshBoth_WithBlacklistedToken_ShouldThrowException() {
        // Arrange
        JwtParsed refreshToken = mock(JwtParsed.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn(JwtTokenType.REFRESH);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshToken.getJti()).thenReturn(testJti);
        when(blacklistedRepo.existsByJti(testJti)).thenReturn(true);

        // Act & Assert
        assertThrows(CredentialsExpiredException.class, () -> authService.refreshBoth(testRefreshToken));
        verify(blacklistedRepo).existsByJti(testJti);
        verify(blacklistedRepo, never()).save(any(BlacklistedRefreshToken.class));
        verify(jwt, never()).generateAccessToken(any(UUID.class));
        verify(jwt, never()).generateRefreshToken(any(UUID.class));
    }

    @Test
    void refreshAccess_WithValidRefreshToken_ShouldReturnNewAccessToken() {
        // Arrange
        JwtParsed refreshToken = mock(JwtParsed.class);
        JwtParsed newAccessToken = mock(JwtParsed.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn(JwtTokenType.REFRESH);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshToken.getJti()).thenReturn(testJti);
        when(refreshToken.getUserId()).thenReturn(testUserId);
        when(blacklistedRepo.existsByJti(testJti)).thenReturn(false);
        when(jwt.generateAccessToken(testUserId)).thenReturn(newAccessToken);

        // Act
        AuthService.TokenPair result = authService.refreshAccess(testRefreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(newAccessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());
        verify(jwt).validateToken(testRefreshToken);
        verify(jwt).parse(testRefreshToken);
        verify(blacklistedRepo).existsByJti(testJti);
        verify(jwt).generateAccessToken(testUserId);
        verify(jwt, never()).generateRefreshToken(any(UUID.class));
        verify(blacklistedRepo, never()).save(any(BlacklistedRefreshToken.class));
    }

    @Test
    void logout_WithValidRefreshToken_ShouldBlacklistToken() {
        // Arrange
        JwtParsed refreshToken = mock(JwtParsed.class);
        ArgumentCaptor<BlacklistedRefreshToken> tokenCaptor = ArgumentCaptor.forClass(BlacklistedRefreshToken.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn(JwtTokenType.REFRESH);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshToken.getJti()).thenReturn(testJti);
        when(blacklistedRepo.existsByJti(testJti)).thenReturn(false);

        // Act
        authService.logout(testRefreshToken);

        // Assert
        verify(blacklistedRepo).existsByJti(testJti);
        verify(blacklistedRepo).save(tokenCaptor.capture());

        BlacklistedRefreshToken savedToken = tokenCaptor.getValue();
        assertEquals(testJti, savedToken.getJti());
        assertNotNull(savedToken.getCreatedAt());
    }

    @Test
    void logout_WithAlreadyBlacklistedToken_ShouldNotSaveAgain() {
        // Arrange
        JwtParsed refreshToken = mock(JwtParsed.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn(JwtTokenType.REFRESH);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshToken.getJti()).thenReturn(testJti);
        when(blacklistedRepo.existsByJti(testJti)).thenReturn(true);

        // Act
        authService.logout(testRefreshToken);

        // Assert
        verify(blacklistedRepo).existsByJti(testJti);
        verify(blacklistedRepo, never()).save(any(BlacklistedRefreshToken.class));
    }

    @Test
    void parseAndValidateRefresh_WithInvalidToken_ShouldThrowJwtException() {
        // Arrange
        when(jwt.validateToken(testRefreshToken)).thenReturn(false);

        // Act & Assert
        assertThrows(JwtException.class, () -> authService.refreshBoth(testRefreshToken));
        verify(jwt).validateToken(testRefreshToken);
        verify(jwt, never()).parse(anyString());
    }

    @Test
    void parseAndValidateRefresh_WithWrongTokenType_ShouldThrowJwtException() {
        // Arrange
        JwtParsed token = mock(JwtParsed.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(token);
        when(token.getType()).thenReturn(JwtTokenType.ACCESS); // Wrong type

        // Act & Assert
        assertThrows(JwtException.class, () -> authService.refreshBoth(testRefreshToken));
        verify(jwt).validateToken(testRefreshToken);
        verify(jwt).parse(testRefreshToken);
    }

    @Test
    void parseAndValidateRefresh_WithExpiredToken_ShouldThrowJwtException() {
        // Arrange
        JwtParsed token = mock(JwtParsed.class);

        when(jwt.validateToken(testRefreshToken)).thenReturn(true);
        when(jwt.parse(testRefreshToken)).thenReturn(token);
        when(token.getType()).thenReturn(JwtTokenType.REFRESH);
        when(token.getExpiresAt()).thenReturn(Instant.now().minusSeconds(3600)); // Expired

        // Act & Assert
        assertThrows(JwtException.class, () -> authService.refreshBoth(testRefreshToken));
        verify(jwt).validateToken(testRefreshToken);
        verify(jwt).parse(testRefreshToken);
    }


}
