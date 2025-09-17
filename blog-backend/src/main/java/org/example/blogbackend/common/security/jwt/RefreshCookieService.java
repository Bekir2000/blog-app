package org.example.blogbackend.common.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.common.config.RefreshCookieProperties;
import org.example.blogbackend.common.utils.CookieUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshCookieService {
    private final RefreshCookieProperties props;

    // convenience: pass the parsed token
    public void attach(HttpServletResponse res, JwtParsed refresh) {
        attach(res, refresh.getValue(), refresh.getExpiresAt());
    }

    // precise: compute TTL from expiry
    public void attach(HttpServletResponse res, String refreshValue, Instant expiresAt) {
        long ttl = Math.max(0, expiresAt.getEpochSecond() - Instant.now().getEpochSecond());
        attach(res, refreshValue, ttl);
    }

    // fallback: explicit TTL or property default
    public void attach(HttpServletResponse res, String refreshValue, long ttlSecondsFallback) {
        long maxAge = (ttlSecondsFallback > 0 ? ttlSecondsFallback : props.getMaxAgeSeconds());
        // cap to int for Servlet cookies
        int maxAgeInt = (maxAge > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) maxAge;

        CookieUtils.addHttpOnlyCookie(
                res,
                props.getName(),
                refreshValue,
                maxAgeInt,
                props.isSecure(),
                props.getSameSite(),
                props.getPath(),
                props.getDomain()
        );
    }

    public void clear(HttpServletResponse res) {
        CookieUtils.clearCookie(res, props.getName(), props.isSecure(), props.getSameSite(),
                props.getPath(), props.getDomain());
    }

    public String read(HttpServletRequest req) {
        return CookieUtils.getCookieValue(req, props.getName());
    }
}


