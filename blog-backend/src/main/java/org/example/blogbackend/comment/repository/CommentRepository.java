package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // 1. Fetch Root Comments
    // Note: We do NOT fetch replies here. @BatchSize in entity handles that efficiently.
    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId AND c.parent IS NULL",
            countQuery = "SELECT count(c) FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL")
    Page<Comment> findAllRootCommentsByPostId(@Param("postId") UUID postId, Pageable pageable);

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    // 2. Optimization: Check if a comment is a root WITHOUT loading the entity
    @Query("SELECT (c.parent IS NULL) FROM Comment c WHERE c.id = :id")
    Optional<Boolean> isRootComment(@Param("id") UUID id);

    // 3. Native Like Queries
    @Query(value = "SELECT COUNT(1) > 0 FROM comment_likes WHERE comment_id = :commentId AND user_id = :userId", nativeQuery = true)
    boolean existsLikeByCommentIdAndUserId(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO comment_likes (comment_id, user_id) VALUES (:commentId, :userId)", nativeQuery = true)
    void addLike(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM comment_likes WHERE comment_id = :commentId AND user_id = :userId", nativeQuery = true)
    void removeLike(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Comment c SET c.likesCount = c.likesCount + 1 WHERE c.id = :commentId")
    void incrementLikes(@Param("commentId") UUID commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.likesCount = c.likesCount - 1 WHERE c.id = :commentId AND c.likesCount > 0")
    void decrementLikes(@Param("commentId") UUID commentId);
}
