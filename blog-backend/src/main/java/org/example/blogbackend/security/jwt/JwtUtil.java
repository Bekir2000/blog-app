package org.example.blogbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.example.blogbackend.config.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final Map<JwtTokenType, Long> ttlMs = new EnumMap<>(JwtTokenType.class);

    public JwtUtil(JwtProperties props) {
        byte[] raw = Base64.getDecoder().decode(props.getSecret()); // base64 → bytes
        this.key = Keys.hmacShaKeyFor(raw);                         // HS256/384/512, len >= 32 bytes
        ttlMs.put(JwtTokenType.ACCESS,  props.getAccessExp().toMillis());
        ttlMs.put(JwtTokenType.REFRESH, props.getRefreshExp().toMillis());
    }

    /* ===================== build ===================== */

    public JwtParsed generateAccessToken(UUID userId)  { return generate(userId, JwtTokenType.ACCESS); }
    public JwtParsed generateRefreshToken(UUID userId) { return generate(userId, JwtTokenType.REFRESH); }

    private JwtParsed generate(UUID userId, JwtTokenType type) {
        long ttl = ttlMs.get(type);
        Instant now = Instant.now();
        Instant exp = now.plusMillis(ttl);

        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())            // jti
                .subject(userId.toString())                             // sub (UUID string)
                .issuedAt(Date.from(now))                    // iat
                .expiration(Date.from(exp))                  // exp
                .claim("typ", type.name().toLowerCase())     // "access" | "refresh"
                .signWith(key)
                .compact();

        // return parsed view so caller immediately has metadata
        return new JwtParsed(token, type, userId,
                UUID.fromString(Jwts.parser().verifyWith(key).build()
                        .parseSignedClaims(token).getPayload().getId()),
                now, exp);
    }

    /* ===================== parse/validate ===================== */

    private Jws<Claims> parseJws(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public boolean validateToken(String token) {
        try { parseJws(token); return true; } catch (JwtException e) { return false; }
    }

    public JwtParsed parse(String token) {
        Claims c = parseJws(token).getPayload();
        String typ = c.get("typ", String.class);
        JwtTokenType t = JwtTokenType.valueOf(typ.toUpperCase());
        return new JwtParsed(
                token,
                t,
                UUID.fromString(c.getSubject()),
                UUID.fromString(c.getId()),
                c.getIssuedAt().toInstant(),
                c.getExpiration().toInstant()
        );
    }
}

