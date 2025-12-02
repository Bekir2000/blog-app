package org.example.blogbackend.user.repository;

import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Don't forget this import
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Optimized: Fetch only IDs of bookmarked posts for a specific list of posts
    @Query("""
    SELECT p.id FROM User u
    JOIN u.bookmarkedPosts p
    WHERE u.id = :userId AND p.id IN :postIds
    """)
    Set<UUID> findBookmarkedPostIdsByUserIdAndPostIdIn(
            @Param("userId") UUID userId,
            @Param("postIds") Set<UUID> postIds
    );

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastActiveAt = :now WHERE u.id = :id")
    void updateLastActiveAt(@Param("id") UUID id, @Param("now") Instant now);

    // --- FIXES START HERE ---

    // 1. Check if user follows author
    // Assumes your User entity has a Set<User> called 'following'
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User u JOIN u.following f " +
            "WHERE u.id = :userId AND f.id = :targetUserId")
    boolean isFollowing(@Param("userId") UUID userId, @Param("targetUserId") UUID targetUserId);

    // 2. Check if user bookmarked a post
    // Assumes your User entity has a Set<Post> called 'bookmarkedPosts'
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User u JOIN u.bookmarkedPosts p " +
            "WHERE u.id = :userId AND p.id = :postId")
    boolean isBookmarked(@Param("userId") UUID userId, @Param("postId") UUID postId);
}
