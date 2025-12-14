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
    // 1. DYNAMIC FEED QUERY (Handles all filters)
    // ========================================================================

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.tags t " + // Join tags for efficient filtering
            "WHERE p.status = :status " +

            // ✅ FIX 1: Corrected Typo and Logic (Compare to Entity field, not string)
            "AND (:authorId IS NULL OR p.author.id = :authorId) " +

            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +

            // ✅ FIX 2: Use JOIN alias for tag filtering (Safer than MEMBER OF)
            "AND (:tagId IS NULL OR t.id = :tagId) " +

            "AND (:query IS NULL OR :query = '' OR " +
            "    (:searchType = 'TITLE' AND LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            // Note: Ensure p.content is TEXT type in DB if you use this line
            "    (:searchType = 'CONTENT' AND LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "    ((:searchType IS NULL OR :searchType = 'MIXED') AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))))" +
            ")")
    Page<PostCardView> findFilteredPosts(
            @Param("status") PostStatus status,
            @Param("query") String query,
            @Param("authorId") UUID authorId,
            @Param("searchType") String searchType,
            @Param("categoryId") UUID categoryId,
            @Param("tagId") UUID tagId,
            Pageable pageable
    );

    // ========================================================================
    // 2. USER SPECIFIC QUERIES
    // ========================================================================

    List<PostCardView> findProjectedByAuthor_IdAndStatus(UUID userId, PostStatus status);

    @Query("SELECT p FROM User u JOIN u.bookmarkedPosts p WHERE u.id = :userId")
    Page<PostCardView> findBookmarkedPostsByUserId(@Param("userId") UUID userId, Pageable pageable);

    // ========================================================================
    // 3. ATOMIC UPDATES & NATIVE LIKES
    // ========================================================================

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

    @Query(value = "SELECT COUNT(1) > 0 FROM posts_liked_by WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    boolean existsByPostIdAndUserId(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO posts_liked_by (post_id, user_id) VALUES (:postId, :userId)", nativeQuery = true)
    void addLike(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM posts_liked_by WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    void removeLike(@Param("postId") UUID postId, @Param("userId") UUID userId);
}
