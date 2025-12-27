package org.example.blogbackend.user.service;

import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.user.model.dto.response.UserResponse;

import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface UserService {

    UUID register(String firstName,
                  String lastName,
                  String email,
                  String password,
                  String profileImageUrl);

    UserResponse getById(UUID id);

    void followUser(UUID currentUserId, UUID targetUserId);

    void unfollowUser(UUID currentUserId, UUID targetUserId);

    Set<UserResponse> getFollowingList(UUID userId);

    void createBookmark(UUID postId, UUID userId);

    PagedResponse<PostCardResponse> getBookmarksForUser(
            UUID userId,
            String title,
            Category category,
            String tag,
            String authorName,
            Pageable pageable);

    void deleteBookmark(UUID postId, UUID userId);
}