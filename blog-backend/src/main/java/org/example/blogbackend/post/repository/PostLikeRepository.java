package org.example.blogbackend.post.repository;

import org.example.blogbackend.post.model.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);
    void deleteByPostIdAndUserId(UUID postId, UUID userId);
    @Modifying
    @Query(
            value = "INSERT INTO post_likes (id, post_id, user_id) VALUES (:id, :postId, :userId)",
            nativeQuery = true
    )
    void addByPostIdAndUserId(@Param("id") UUID id, @Param("postId") UUID postId, @Param("userId") UUID userId);
}
