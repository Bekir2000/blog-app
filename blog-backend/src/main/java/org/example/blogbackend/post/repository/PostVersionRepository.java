package org.example.blogbackend.post.repository;

import org.example.blogbackend.post.model.entity.PostVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PostVersionRepository extends
        JpaRepository<PostVersion, UUID> {

    @Query("""
        SELECT v
        FROM PostVersion v
        JOIN v.post p
        WHERE p.author.id = :userId
        AND v.status = 'DRAFT'
    """)
    Page<PostVersion> findDraftsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
