package org.example.blogbackend.post.repository;

import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.projection.PostCardView;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    // --- Feed Queries (Strictly Projections) ---

    // 1. Filter by Category AND Tag
    Page<PostCardView> findProjectedByStatusAndCategory_IdAndTags_Id(
            PostStatus status,
            UUID categoryId,
            UUID tagId,
            Pageable pageable
    );

    // 2. Filter by Category only
    Page<PostCardView> findProjectedByStatusAndCategory_Id(
            PostStatus status,
            UUID categoryId,
            Pageable pageable
    );

    // 3. Filter by Tag only
    Page<PostCardView> findProjectedByStatusAndTags_Id(
            PostStatus status,
            UUID tagId,
            Pageable pageable
    );

    // 4. General Feed (No filters)
    Page<PostCardView> findProjectedByStatus(
            PostStatus status,
            Pageable pageable
    );


    // --- Admin / Author Queries ---

    List<PostCardView> findProjectedByAuthor_IdAndStatus(UUID userId, PostStatus status);

    // --- Comment Optimization ---

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :postId")
    void incrementCommentsCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount - 1 WHERE p.id = :postId AND p.commentsCount > 0")
    void decrementCommentsCount(@Param("postId") UUID postId);
}
