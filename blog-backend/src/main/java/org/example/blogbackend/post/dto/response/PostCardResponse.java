package org.example.blogbackend.post.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blogbackend.category.model.dto.response.CategoryResponse;
import org.example.blogbackend.tag.model.dto.response.TagResponse;
import org.example.blogbackend.user.model.dto.response.UserResponse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PostCardResponse {
    private UUID id;
    private String title;
    private String description; // Short summary only
    private String imageUrl;

    private UserResponse author;
    private CategoryResponse category;
    private Set<TagResponse> tags;

    private Integer views;
    private Integer likes;
    private Integer commentsCount;
    private Integer readingTime;

    private Boolean isBookmarked; // User specific

    private Instant createdAt;
    private Instant updatedAt;
}
