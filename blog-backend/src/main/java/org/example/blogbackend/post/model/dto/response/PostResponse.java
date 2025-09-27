package org.example.blogbackend.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.category.model.dto.response.CategoryResponse;
import org.example.blogbackend.tag.model.dto.response.TagResponse;
import org.example.blogbackend.post.model.PostStatus;

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
    private String description;
    private String imageUrl;
    private UserResponse author;
    private CategoryResponse category;
    private Set<TagResponse> tags;
    private Integer views;
    private Integer likes;
    private Integer commentsCount;
    private Integer readingTime;
    private PostStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
