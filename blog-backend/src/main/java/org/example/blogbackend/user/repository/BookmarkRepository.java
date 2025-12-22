package org.example.blogbackend.user.repository;

import org.example.blogbackend.post.model.projection.PublishedPostCardProjection;
import org.example.blogbackend.user.model.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.post.id = :postId AND b.user.id = :userId")
    int deleteByPostIdAndUserId(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Query("""
        SELECT new org.example.blogbackend.post.model.projection.PublishedPostCardProjection(
            p.id,
            v.title,
            v.description,
            v.imageUrl,
            a.firstName,
            a.lastName,
            a.profileImageUrl,
            p.likeCount,
            p.commentCount,
            p.readingTime,
            (CASE WHEN EXISTS (SELECT 1 FROM PostLike l WHERE l.post = p AND l.userId = :userId) THEN true ELSE false END),
            true,
            p.createdAt
        )
        FROM Post p
        INNER JOIN p.publishedVersion v
        INNER JOIN p.author a
    """)
    Page<PublishedPostCardProjection> findBookmarksByUserId(
            @Param("userId") UUID userId,
            Pageable pageable
    );
}
