package org.example.blogbackend.post.repository;

import org.example.blogbackend.post.model.entity.PostVersion;
import org.example.blogbackend.post.model.projection.DraftCardProjection;
import org.example.blogbackend.post.model.projection.DraftDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostVersionRepository extends
        JpaRepository<PostVersion, UUID> {

    @Query("""
        SELECT new org.example.blogbackend.post.model.projection.DraftCardProjection(
            v.id,
            v.title,
            v.imageUrl,
            v.description,
            v.updatedAt,
            v.createdAt
        )
        FROM PostVersion v
        JOIN v.post p
        WHERE p.author.id = :userId
        AND v.status = 'DRAFT'
    """)
    Page<DraftCardProjection> findDraftsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
