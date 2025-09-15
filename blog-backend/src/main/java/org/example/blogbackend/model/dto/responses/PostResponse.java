package org.example.blogbackend.model.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blogbackend.model.enums.PostStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private AuthorResponse author;
    private CategoryResponse category;
    private Set<TagResponse> tags;
    private Integer readingTime;
    private Instant createdAt;
    private Instant updatedAt;
    private PostStatus status;
}
