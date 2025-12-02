package org.example.blogbackend.post.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.blogbackend.post.model.PostStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostResponse extends PostCardResponse {

    // 1. Only include fields that are NOT in PostCardResponse
    private String content;
    private PostStatus status;
}
