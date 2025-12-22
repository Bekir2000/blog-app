package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.comment.model.projection.CommentWithDetails;
import org.example.blogbackend.post.model.projection.PublishedPostCardProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    @Query("""
    SELECT new org.example.blogbackend.comment.model.projection.CommentWithDetails(
        c,
        a.firstName,
        a.lastName,
        a.profileImageUrl,
        (CASE WHEN EXISTS (
            SELECT 1 FROM CommentLike l
            WHERE l.commentId = c.id AND l.userId = :userId
        ) THEN true ELSE false END)
    )
    FROM Comment c
    JOIN User a ON c.authorId = a.id
    WHERE c.postId = :postId
      AND c.parent IS NULL
    ORDER BY c.createdAt DESC
""")
    Page<CommentWithDetails> findRootComments(
            UUID postId,
            UUID userId,
            Pageable pageable
    );


    @Query("""
    SELECT new org.example.blogbackend.comment.model.projection.CommentWithDetails(
        c,
        a.firstName,
        a.lastName,
        a.profileImageUrl,
        (CASE WHEN EXISTS (
            SELECT 1 FROM CommentLike l
            WHERE l.commentId = c.id AND l.userId = :userId
        ) THEN true ELSE false END)
    )
    FROM Comment c
    JOIN User a ON c.authorId = a.id
    WHERE c.postId = :postId
      AND c.parent IS NOT NULL
""")
    List<CommentWithDetails> findAllReplies(UUID postId, UUID userId);


    @Modifying
    @Query("""
    update Comment c
    set c.likeCount = c.likeCount + 1
    where c.id = :id
    """)
    void incrementLike(UUID id);

    @Modifying
    @Query("""
    update Comment c
    set c.likeCount = c.likeCount - 1
    where c.id = :id and c.likeCount > 0
    """)
    void decrementLike(UUID id);

    @Modifying
    @Query("""
    update Comment c
    set c.replyCount = c.replyCount + 1
    where c.id = :commentId
    """)
    void incrementReplyCount(UUID commentId);

    @Modifying
    @Query("""
    update Comment c
    set c.replyCount = c.replyCount - 1
    where c.id = :commentId and c.replyCount > 0
    """)
    void decrementReplyCount(UUID commentId);

}
