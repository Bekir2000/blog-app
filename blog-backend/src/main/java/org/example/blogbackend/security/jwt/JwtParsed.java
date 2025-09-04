package org.example.blogbackend.security.jwt;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class JwtParsed {
    private final String value;
    private final JwtTokenType type;
    private final UUID userId;
    private final UUID jti;
    private final Instant issuedAt;
    private final Instant expiresAt;
}
