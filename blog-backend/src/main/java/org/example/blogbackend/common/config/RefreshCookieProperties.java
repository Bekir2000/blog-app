package org.example.blogbackend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.refresh-cookie")
public class RefreshCookieProperties {
    private String name = "refresh_token";
    private String path = "/api/v1/auth";
    private boolean secure = true;           // set false for local dev
    private String sameSite = "Strict";      // Strict / Lax / None
    private String domain;                   // optional
    private long maxAgeSeconds = 14 * 24 * 60 * 60; // 14 days
}
