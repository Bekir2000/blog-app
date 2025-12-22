package org.example.blogbackend.comment.model.projection;

import org.example.blogbackend.comment.model.entity.Comment;

public record CommentWithDetails(
        Comment comment,
        String authorFirstName,
        String authorLastName,
        String authorImageUrl,
        boolean likedByCurrentUser
) {
}
