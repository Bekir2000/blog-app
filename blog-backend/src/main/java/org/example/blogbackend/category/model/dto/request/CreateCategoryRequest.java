package org.example.blogbackend.category.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCategoryRequest(
        UUID id,
        @NotBlank(message = "Category name cannot be blank")
        @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-_ ]+$", message = "Category name can only contain letters, numbers, dashes, and spaces")
        String name
) {}
