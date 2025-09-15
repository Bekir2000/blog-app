package org.example.blogbackend.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.blogbackend.model.enums.PostStatus;

import java.util.Set;
import java.util.UUID;

public record UpdatePostRequest(
    @NotNull(message = "Post ID is required")
    UUID id,

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters")
    String title,

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
    String content,

    @NotNull(message = "Category is required")
    CreateCategoryRequest category,

    @Size(max = 10, message = "You can only add up to {max} tags")
    Set<CreateTagRequest> tags,

    @NotNull(message = "Post status is required")
    PostStatus status
) {}
