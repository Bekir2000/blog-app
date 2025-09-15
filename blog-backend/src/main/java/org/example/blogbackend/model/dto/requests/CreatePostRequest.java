package org.example.blogbackend.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.blogbackend.model.enums.PostStatus;

import java.util.Set;

public record CreatePostRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    String title,

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
    String content,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Category is required")
    CreateCategoryRequest category,

    @NotEmpty(message = "Tag names cannot be empty")
    @Size(max = 10, message = "You can only add up to {max} tags")
    Set<CreateTagRequest> tags,

    @NotNull(message = "Post status is required")
    PostStatus status
) {}
