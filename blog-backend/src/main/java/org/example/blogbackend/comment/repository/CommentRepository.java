package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.user.model.entity.User; // Ensure this import is correct
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // =================================================================================
    // 1. READ OPERATIONS (The "Single Query" Projections)
    // =================================================================================

    // A. For Logged-In Users: Calculates "likedByCurrentUser" = true/false
    @Query("""
       SELECT new org.example.blogbackend.comment.model.dto.response.CommentResponse(
           c.id,
           c.content,
           c.author,
           c.post.id,
           SIZE(c.likedBy),
           (CASE WHEN :currentUser MEMBER OF c.likedBy THEN true ELSE false END),
           SIZE(c.replies)
       )
       FROM Comment c
       WHERE c.post.id = :postId AND c.parent IS NULL
       ORDER BY c.createdAt DESC
    """)
    Page<CommentResponse> findRootCommentsForUser(
            @Param("postId") UUID postId,
            @Param("currentUser") User currentUser,
            Pageable pageable
    );

    // B. For Guests: Hardcodes "likedByCurrentUser" = false (Faster)
    @Query("""
       SELECT new org.example.blogbackend.comment.model.dto.response.CommentResponse(
           c.id,
           c.content,
           c.author,
           c.post.id,
           SIZE(c.likedBy),
           false,
           SIZE(c.replies)
       )
       FROM Comment c
       WHERE c.post.id = :postId AND c.parent IS NULL
       ORDER BY c.createdAt DESC
    """)
    Page<CommentResponse> findRootCommentsForGuest(
            @Param("postId") UUID postId,
            Pageable pageable
    );

    // =================================================================================
    // 2. UTILITY & CHECKS
    // =================================================================================

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    // Optimization: Check if a comment is a root WITHOUT loading the entity
    @Query("SELECT (c.parent IS NULL) FROM Comment c WHERE c.id = :id")
    Optional<Boolean> isRootComment(@Param("id") UUID id);

    // =================================================================================
    // 3. WRITE OPERATIONS (Likes)
    // =================================================================================

    // Note: Since you use native queries for performance here, we keep them.
    // Ideally, you would use: comment.getLikedBy().add(user) + save(comment)
    // But these are faster for simple toggles.

    @Query(value = "SELECT COUNT(1) > 0 FROM comment_likes WHERE comment_id = :commentId AND user_id = :userId", nativeQuery = true)
    boolean existsLikeByCommentIdAndUserId(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO comment_likes (comment_id, user_id) VALUES (:commentId, :userId)", nativeQuery = true)
    void addLike(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM comment_likes WHERE comment_id = :commentId AND user_id = :userId", nativeQuery = true)
    void removeLike(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    // ⚠️ REMOVED: incrementLikes / decrementLikes
    // Reason: We now use SIZE(c.likedBy) in the read query, so we don't need to maintain a counter column.
}
