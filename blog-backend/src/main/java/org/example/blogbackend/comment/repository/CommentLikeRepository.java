package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.comment.model.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {
    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    void deleteByCommentIdAndUserId(UUID commentId, UUID userId);
}
