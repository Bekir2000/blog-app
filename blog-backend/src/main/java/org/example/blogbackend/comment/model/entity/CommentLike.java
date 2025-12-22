package org.example.blogbackend.comment.model.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "comment_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_user_like",
                columnNames = {"comment_id", "user_id"}
        ))
public class CommentLike {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    protected CommentLike() {}

    public CommentLike(UUID commentId, UUID userId) {
        this.commentId = commentId;
        this.userId = userId;
    }
}

