package org.example.blogbackend.comment.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Content is required")
        @Size(min = 10, max = 2000, message = "Content must be between {min} and {max} characters")
        String content
) {
}
