package org.example.blogbackend.model.dto.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blogbackend.model.enums.PostStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostRequest {

    @NotNull(message = "Post ID is required")
    private UUID id;

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
    private String content;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @Builder.Default
    @Size(max = 10, message = "You can only add up to {max} tags")
    private Set<UUID> tagIds = new HashSet<>();

    @NotNull(message = "Post status is required")
    private PostStatus status;
}
