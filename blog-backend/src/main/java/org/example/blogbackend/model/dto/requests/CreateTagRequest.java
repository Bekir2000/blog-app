package org.example.blogbackend.model.dto.requests;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTagRequest(
    UUID id,
    @Size(min = 3, max = 30, message = "Tag name must be between {min} and {max} characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_ ]+$", message = "Tag name can only contain letters, numbers, dashes, and spaces")
    String name
) {}
