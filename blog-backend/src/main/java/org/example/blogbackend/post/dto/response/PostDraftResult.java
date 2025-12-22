package org.example.blogbackend.post.dto.response;

import java.util.UUID;

public record PostDraftResult(
        UUID postId,
        UUID draftId
) {
}
