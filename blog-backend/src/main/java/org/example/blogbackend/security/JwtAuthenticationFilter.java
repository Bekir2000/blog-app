package org.example.blogbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogbackend.model.entities.User;
import org.example.blogbackend.security.jwt.JwtParsed;
import org.example.blogbackend.security.jwt.JwtTokenType;
import org.example.blogbackend.security.jwt.JwtUtil;
import org.example.blogbackend.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService; // must have findById(UUID) or similar

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null && jwtUtil.validateToken(token)) {
                JwtParsed parsed = jwtUtil.parse(token);
                if (parsed.getType() == JwtTokenType.REFRESH) { chain.doFilter(request, response); return; }

                UUID userId = parsed.getUserId();
                User user = userService.getById(userId); // throws if not found
                BlogUserDetails bud = new BlogUserDetails(user);

                var auth = new UsernamePasswordAuthenticationToken(bud, null, bud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                request.setAttribute("userId", userId);
            }
        } catch (Exception ex) {
            log.warn("Invalid JWT: {}", ex.getMessage());
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }
}


