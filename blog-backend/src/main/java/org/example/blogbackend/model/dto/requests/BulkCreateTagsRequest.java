package org.example.blogbackend.model.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BulkCreateTagsRequest(
    @NotEmpty(message = "Tag names cannot be empty")
    @Size(max = 10, message = "You can only add up to {max} tags")
    Set<CreateTagRequest> tags
) {
}
