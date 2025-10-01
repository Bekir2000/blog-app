package org.example.blogbackend.user.repository;

import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
