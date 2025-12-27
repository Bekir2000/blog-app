package org.example.blogbackend.shared.security;

import lombok.RequiredArgsConstructor;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InputSanitizer {

    private final PolicyFactory plainTextPolicy;
    private final List<String> allowedImageHosts;

    /**
     * Sanitizes plain text input by stripping all HTML tags.
     */
    public String sanitizeText(String input) {
        if (input == null) return null;
        return plainTextPolicy.sanitize(input);
    }

    /**
     * Sanitizes a set of tags (e.g. for Posts).
     */
    public Set<String> sanitizeTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }
        return tags.stream()
                .map(this::sanitizeText)
                .collect(Collectors.toSet());
    }

    /**
     * Validates and sanitizes an Image URL.
     * Checks protocol (http/https) and allowed domains.
     */
    public String sanitizeUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            String trimmedUrl = url.trim();

            if (trimmedUrl.length() > 2048) {
                throw new IllegalArgumentException("URL is too long");
            }

            URI uri = URI.create(trimmedUrl);

            // 1. Protocol Check
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Invalid image URL: Only HTTP and HTTPS protocols are allowed");
            }

            // 2. Domain Check
            String host = uri.getHost();
            if (host == null) {
                throw new IllegalArgumentException("Invalid image URL: Host is missing");
            }

            // Check against allowed hosts
            boolean isAllowed = allowedImageHosts.stream()
                    .anyMatch(allowed -> host.equalsIgnoreCase(allowed) || host.endsWith("." + allowed));

            if (!isAllowed) {
                throw new IllegalArgumentException("Image domain not allowed. Allowed hosts: " + allowedImageHosts);
            }

            return trimmedUrl;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid image URL syntax");
        }
    }
}
