package org.example.blogbackend.user.service;

import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {

    // Existing methods
    void register(RegisterRequest req);
    User getById(UUID id);
    List<Boolean> getPostBookmarkStatuses(UUID userId, List<UUID> postIds);
    void createBookmark(UUID postId, UUID userId);
    void deleteBookmark(UUID postId, UUID userId);

    // --- FOLLOWING METHODS ---

    /**
     * Makes the current user follow the target user.
     */
    void followUser(UUID currentUserId, UUID targetUserId);

    /**
     * Makes the current user unfollow the target user.
     */
    void unfollowUser(UUID currentUserId, UUID targetUserId);

    /**
     * Retrieves the list of users that the specific user is following.
     */
    Set<User> getFollowingList(UUID userId);
}