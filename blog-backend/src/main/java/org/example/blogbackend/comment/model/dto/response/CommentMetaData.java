package org.example.blogbackend.comment.model.dto.response;

public record CommentMetaData(
        long likeCount,
        long replyCount,
        boolean likedByCurrentUser
) {
}
