package org.example.blogbackend.user.repository;

import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
    select p.id from User u
    join u.bookmarkedPosts p
    where u.id = :userId and p.id in :postIds
    """)
    List<UUID> findBookmarkedPostIdsByUserIdAndPostIdIn(UUID userId, List<UUID> postIds);

    // Efficiently update only the timestamp
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastActiveAt = :now WHERE u.id = :id")
    void updateLastActiveAt(UUID id, Instant now);
}
