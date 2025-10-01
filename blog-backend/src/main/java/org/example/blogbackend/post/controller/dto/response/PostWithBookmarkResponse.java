package org.example.blogbackend.post.controller.dto.response;

/**
 * Combines PostResponse data with an isBookmarked flag.
 */
public record PostWithBookmarkResponse(
        PostResponse post,
        boolean isBookmarked
) {
}
