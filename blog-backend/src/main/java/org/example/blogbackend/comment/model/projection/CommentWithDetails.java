package org.example.blogbackend.comment.model.projection;

import org.example.blogbackend.comment.model.entity.Comment;

import java.util.UUID;

public record CommentWithDetails(
        Comment comment,
        UUID authorId,
        String authorFirstName,
        String authorLastName,
        String authorImageUrl,
        boolean likedByCurrentUser
) {
}
