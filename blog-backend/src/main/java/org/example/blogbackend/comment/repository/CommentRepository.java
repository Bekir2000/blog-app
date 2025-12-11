package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // =================================================================================
    // 1. READ OPERATIONS (Return Entities for Nesting)
    // =================================================================================

    // We fetch the AUTHOR eagerly to avoid N+1 queries.
    // We do NOT fetch replies eagerly; @BatchSize in the entity handles that efficiently.
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findRootComments(@Param("postId") UUID postId, Pageable pageable);

    // BATCH CHECK: Checks "Is Liked" for a whole list of comments in 1 query.
    // This replaces the "MEMBER OF" check in the previous projection.
    @Query("SELECT c.id FROM Comment c JOIN c.likedBy user WHERE user.id = :userId AND c IN :comments")
    Set<UUID> findLikedCommentIds(@Param("comments") Collection<Comment> comments, @Param("userId") UUID userId);

    // =================================================================================
    // 2. UTILITY & CHECKS
    // =================================================================================

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    @Query("SELECT (c.parent IS NULL) FROM Comment c WHERE c.id = :id")
    Optional<Boolean> isRootComment(@Param("id") UUID id);

    // =================================================================================
    // 3. WRITE OPERATIONS (Native Native Speed)
    // =================================================================================

    @Query(value = "SELECT COUNT(1) > 0 FROM comment_likes WHERE comment_id = :commentId AND user_id = :userId", nativeQuery = true)
    boolean existsLikeByCommentIdAndUserId(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO comment_likes (comment_id, user_id) VALUES (:commentId, :userId)", nativeQuery = true)
    void addLike(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM comment_likes WHERE comment_id = :commentId AND user_id = :userId", nativeQuery = true)
    void removeLike(@Param("commentId") UUID commentId, @Param("userId") UUID userId);
}
