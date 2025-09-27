package org.example.blogbackend.comment.model.dto.response;

import org.example.blogbackend.user.model.dto.response.UserResponse;

import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        UserResponse author,
        UUID postId
) {
}
