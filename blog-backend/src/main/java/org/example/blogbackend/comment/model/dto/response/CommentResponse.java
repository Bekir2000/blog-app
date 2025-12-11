package org.example.blogbackend.comment.model.dto.response;

import org.example.blogbackend.user.model.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        UserResponse author,
        UUID postId,
        int likesCount,   // New: Shows how many likes
        int replyCount,   // New: Shows how many replies
        List<CommentResponse> replies
) {
}