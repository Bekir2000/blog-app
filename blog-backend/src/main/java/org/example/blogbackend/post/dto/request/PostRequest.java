package org.example.blogbackend.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.tag.model.dto.request.CreateTagRequest;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

public record PostRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Content is required")
        @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
        String content,

        @URL(message = "Image URL must be a valid URL")
        @NotBlank(message = "Image URL is required")
        @Size(max = 2048, message = "Image URL must not exceed {max} characters")
        String imageUrl,

        @NotNull(message = "Category is required")
        CreateCategoryRequest category,

        @NotEmpty(message = "Tag names cannot be empty")
        @Size(max = 10, message = "You can only add up to {max} tags")
        Set<CreateTagRequest> tags,

        @NotNull(message = "Post status is required")
        PostStatus status
) {
}
