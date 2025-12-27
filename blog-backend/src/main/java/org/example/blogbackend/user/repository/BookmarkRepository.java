package org.example.blogbackend.user.repository;

import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.model.projection.PostWithDetailsDto;
import org.example.blogbackend.user.model.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.post.id = :postId AND b.user.id = :userId")
    int deleteByPostIdAndUserId(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Query("""
    SELECT new org.example.blogbackend.post.model.projection.PostWithDetailsDto(
        p,
       (CASE WHEN :userId IS NOT NULL AND EXISTS (SELECT 1 FROM Bookmark b2 WHERE b2.post = p AND b2.user.id = :userId) THEN true ELSE false END),
       (CASE WHEN :userId IS NOT NULL AND EXISTS (SELECT 1 FROM Follow f WHERE f.target = p.author AND f.follower.id = :userId) THEN true ELSE false END),
       (CASE WHEN :userId IS NOT NULL AND EXISTS (SELECT 1 FROM PostLike l WHERE l.post = p AND l.userId = :userId) THEN true ELSE false END)
    )
    FROM Bookmark b
    INNER JOIN b.post p
    INNER JOIN p.publishedVersion v
    INNER JOIN p.author a
    WHERE 
        b.user.id = :userId
        AND (:title IS NULL OR :title = '' OR LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:category IS NULL OR v.category = :category)
        AND (:tag IS NULL OR :tag = '' OR :tag MEMBER OF v.tags)
        AND (:authorName IS NULL OR :authorName = '' OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :authorName, '%')))
""")
    Page<PostWithDetailsDto> findBookmarksByUserId(
            @Param("userId") UUID userId,
            @Param("title") String title,
            @Param("category") Category category,
            @Param("tag") String tag,
            @Param("authorName") String authorName,
            Pageable pageable
    );
}
