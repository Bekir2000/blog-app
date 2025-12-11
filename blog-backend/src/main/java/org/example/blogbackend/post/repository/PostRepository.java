package org.example.blogbackend.post.repository;

import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.projection.PostCardView;
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

    // ========================================================================
    // 1. FEED QUERIES (Read-Only Projections)
    // ========================================================================

    // Filter by Category AND Tag
    Page<PostCardView> findProjectedByStatusAndCategory_IdAndTags_Id(
            PostStatus status,
            UUID categoryId,
            UUID tagId,
            Pageable pageable
    );

    // Filter by Category only
    Page<PostCardView> findProjectedByStatusAndCategory_Id(
            PostStatus status,
            UUID categoryId,
            Pageable pageable
    );

    // Filter by Tag only
    Page<PostCardView> findProjectedByStatusAndTags_Id(
            PostStatus status,
            UUID tagId,
            Pageable pageable
    );

    // General Feed (No filters)
    Page<PostCardView> findProjectedByStatus(
            PostStatus status,
            Pageable pageable
    );

    // ========================================================================
    // 2. USER SPECIFIC QUERIES
    // ========================================================================

    List<PostCardView> findProjectedByAuthor_IdAndStatus(UUID userId, PostStatus status);

    /**
     * Fetches bookmarked posts.
     * Query optimization: Joins from Post -> User to utilize the PostCardView projection efficiently.
     * Assumes a generic ManyToMany or mapped relationship exists.
     */
    @Query("SELECT p FROM Post p JOIN p.bookmarkedByUsers u WHERE u.id = :userId")
    Page<PostCardView> findBookmarkedPostsByUserId(@Param("userId") UUID userId, Pageable pageable);

    // ========================================================================
    // 3. COUNTER OPTIMIZATIONS (Atomic Updates)
    // ========================================================================
    // Note: These should be called inside a @Transactional Service method

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :postId")
    void incrementCommentsCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount - 1 WHERE p.id = :postId AND p.commentsCount > 0")
    void decrementCommentsCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes - 1 WHERE p.id = :postId AND p.likes > 0")
    void decrementLikes(@Param("postId") UUID postId);

    // ========================================================================
    // 4. NATIVE LIKE MANAGEMENT (High Performance)
    // ========================================================================

    @Query(value = "SELECT COUNT(1) > 0 FROM posts_liked_by WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    boolean existsByPostIdAndUserId(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO posts_liked_by (post_id, user_id) VALUES (:postId, :userId)", nativeQuery = true)
    void addLike(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM posts_liked_by WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    void removeLike(@Param("postId") UUID postId, @Param("userId") UUID userId);
}
