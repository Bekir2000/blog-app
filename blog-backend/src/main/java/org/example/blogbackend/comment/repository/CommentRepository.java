package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * Finds a comment strictly matching both the comment ID and the post ID.
     * Useful for ensuring a comment actually belongs to the requested post.
     */
    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    /**
     * Fetches a page of comments for a specific post.
     * * Optimization Details:
     * 1. JOIN FETCH c.author: Eagerly loads the User (author) entity to prevent N+1 queries.
     * 2. countQuery: Explicitly defines how to count rows for pagination.
     * We exclude the JOIN in the count to make it faster.
     */
    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId",
            countQuery = "SELECT count(c) FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findAllByPostId(@Param("postId") UUID postId, Pageable pageable);
}
