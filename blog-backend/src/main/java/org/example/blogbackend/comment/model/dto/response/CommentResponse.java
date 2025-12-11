package org.example.blogbackend.comment.model.dto.response;

import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        UserResponse author,
        UUID postId,
        long likesCount,
        long replyCount,
        boolean likedByCurrentUser,
        List<CommentResponse> replies
) {
    // Custom Constructor for JPQL Projections
    public CommentResponse(
            UUID id,
            String content,
            User authorEntity,       // JPQL sends the User Entity
            UUID postId,
            int likesCount,
            boolean likedByCurrentUser,
            int replyCount
    ) {
        this(
                id,
                content,
                // 👇 FIX: Map ALL 8 fields required by UserResponse
                new UserResponse(
                        authorEntity.getId(),
                        authorEntity.getUsername(),
                        authorEntity.getFirstName(),
                        authorEntity.getLastName(),
                        authorEntity.getProfileImageUrl(),
                        authorEntity.getEmail(),         // Added
                        authorEntity.isOnline(),         // Added (Assumes User entity has isOnline())
                        authorEntity.getLastActiveAt()   // Added (Assumes User entity has getLastActiveAt())
                ),
                postId,
                (long) likesCount,
                (long) replyCount,
                likedByCurrentUser,
                Collections.emptyList()
        );
    }
}