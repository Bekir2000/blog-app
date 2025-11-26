package org.example.blogbackend.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogbackend.common.security.jwt.JwtParsed;
import org.example.blogbackend.common.security.jwt.JwtTokenType;
import org.example.blogbackend.common.security.jwt.JwtUtil;
import org.example.blogbackend.user.repository.UserRepository; // <--- 1. Import Repo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // <--- 2. Inject Repo

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid JWT token");
                } else {
                    JwtParsed parsed = jwtUtil.parse(token);

                    if (parsed.getType() == JwtTokenType.REFRESH) {
                        log.warn("Refresh token used for authentication");
                    } else {
                        // --- 3. TRACK ACTIVITY ---
                        // Update the timestamp in the background
                        // (Ideally, wrap this in an async method for high-performance apps)
                        try {
                            userRepository.updateLastActiveAt(parsed.getUserId(), Instant.now());
                        } catch (Exception e) {
                            // Don't fail the request if tracking fails
                            log.error("Failed to update user activity", e);
                        }

                        BlogUserDetails userDetails = BlogUserDetails.fromJwt(parsed.getUserId(), null, null);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception ex) {
                log.warn("JWT authentication failed: {}", ex.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
