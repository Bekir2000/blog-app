package org.example.blogbackend.shared.security;

import lombok.RequiredArgsConstructor;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InputSanitizer {

    private final PolicyFactory plainTextPolicy;
    private final List<String> allowedImageHosts;

    // We can stick to a simpler, standard regex to avoid headaches
    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    /**
     * Sanitizes plain text input by stripping all HTML tags.
     */
    public String sanitizeText(String input) {
        if (input == null) return null;
        return plainTextPolicy.sanitize(input);
    }

    /**
     * Validates and returns the email.
     * We skip HTML sanitization here because the Regex acts as a strict whitelist,
     * preventing any XSS characters (<, >, etc) automatically.
     */
    public String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Trim whitespace
        String cleanEmail = email.trim();

        // Validate Format directly
        if (!PATTERN.matcher(cleanEmail).matches()) {
            // Include the failing email in the log for easier debugging
            throw new IllegalArgumentException("Invalid email format: '" + cleanEmail + "'");
        }

        return cleanEmail;
    }

    public Set<String> sanitizeTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }
        return tags.stream().map(this::sanitizeText).collect(Collectors.toSet());
    }

    public String sanitizeUrl(String url) {
        if (url == null || url.isBlank()) return null;

        try {
            String trimmedUrl = url.trim();
            if (trimmedUrl.length() > 2048) throw new IllegalArgumentException("URL is too long");

            URI uri = URI.create(trimmedUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Invalid URL: Only HTTP/HTTPS allowed");
            }
            if (host == null) {
                throw new IllegalArgumentException("Invalid URL: Host is missing");
            }

            boolean isAllowed = allowedImageHosts.stream()
                    .anyMatch(allowed -> host.equalsIgnoreCase(allowed) || host.endsWith("." + allowed));

            if (!isAllowed) {
                throw new IllegalArgumentException("Domain not allowed. Allowed: " + allowedImageHosts);
            }

            return trimmedUrl;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL syntax");
        }
    }
}
