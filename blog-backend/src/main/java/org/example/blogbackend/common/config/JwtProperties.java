package org.example.blogbackend.common.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * Base64-encoded secret for HMAC signing.
     */
    @NotBlank
    private String secret = "change-me"; // ⚠️ safe default only for dev

    /**
     * Access token TTL (default: 15 minutes).
     */

    private Duration accessExp = Duration.ofMinutes(15);

    /**
     * Refresh token TTL (default: 14 days).
     */

    private Duration refreshExp = Duration.ofDays(14);
}

