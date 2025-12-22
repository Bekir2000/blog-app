package org.example.blogbackend.user.repository;

import org.example.blogbackend.user.model.entity.Follow;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower.id = :followerId AND f.target.id = :targetId")
    int deleteByFollowerIdAndTargetId(UUID followerId, UUID targetId);

    @Query("SELECT f.target FROM Follow f WHERE f.follower.id = :followerId")
    Set<User> findTargetsByFollowerId(@Param("followerId") UUID followerId);

}
