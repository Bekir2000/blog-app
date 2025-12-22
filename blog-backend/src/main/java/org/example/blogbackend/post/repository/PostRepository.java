package org.example.blogbackend.post.repository;

import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.projection.PublishedPostCardProjection;
import org.example.blogbackend.post.model.projection.PostWithDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {


    @Query("""
   SELECT new org.example.blogbackend.post.model.projection.PostWithDetailsDto(
       p,
       (CASE WHEN EXISTS (SELECT 1 FROM Bookmark b WHERE b.post = p AND b.user.id = :userId) THEN true ELSE false END),
       (CASE WHEN EXISTS (SELECT 1 FROM Follow f WHERE f.target = p.author AND f.follower.id = :userId) THEN true ELSE false END),
       (CASE WHEN EXISTS (SELECT 1 FROM PostLike l WHERE l.post = p AND l.userId = :userId) THEN true ELSE false END)
   )
   FROM Post p
   INNER JOIN FETCH p.publishedVersion
   LEFT JOIN FETCH p.author
   WHERE p.id = :postId
""")
    Optional<PostWithDetailsDto> findPostWithDetails(
            @Param("postId") UUID postId,
            @Param("userId") UUID userId
    );

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
            (CASE WHEN EXISTS (SELECT 1 FROM Bookmark b WHERE b.post = p AND b.user.id = :userId) THEN true ELSE false END),
            p.createdAt
        )
        FROM Post p
        INNER JOIN p.publishedVersion v
        INNER JOIN p.author a
        WHERE 
            (:title IS NULL OR :title = '' OR LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%')))
            AND (:category IS NULL OR v.category = :category)
            
            AND (:tag IS NULL OR :tag = '' OR :tag MEMBER OF v.tags)
            
            AND (:authorName IS NULL OR :authorName = '' OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :authorName, '%')))
    """)
    Page<PublishedPostCardProjection> findPublishedPosts(
            @Param("userId") UUID userId,
            @Param("title") String title,
            @Param("category") Category category,
            @Param("tag") String tag,
            @Param("authorName") String authorName,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :id")
    void decrementLikeCount(@Param("id") UUID postId);

}
