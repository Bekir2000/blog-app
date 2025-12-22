package org.example.blogbackend.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.blogbackend.post.model.Category; // Import your Enum
import org.hibernate.validator.constraints.URL;

import java.util.Set;

public record PostDraftRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
        String content,

        @URL(message = "Image URL must be a valid URL")
        @Size(max = 2048, message = "Image URL must not exceed {max} characters")
        String imageUrl,

        Category category,

        @Size(max = 10, message = "You can add up to 10 tags")
        Set<String> tags
) {
}
