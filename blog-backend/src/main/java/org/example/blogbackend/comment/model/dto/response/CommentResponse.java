package org.example.blogbackend.comment.model.dto.response;

import org.example.blogbackend.user.model.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        UserResponse author,
        UUID postId,
        long likesCount,        // Changed to long (matches Entity @Formula)
        long replyCount,        // Changed to long (matches Entity @Formula)
        boolean likedByCurrentUser,
        List<CommentResponse> replies
) {
}