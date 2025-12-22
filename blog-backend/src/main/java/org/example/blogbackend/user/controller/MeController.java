package org.example.blogbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.security.BlogUserDetails;
import org.example.blogbackend.user.mapper.UserMapper;
import org.example.blogbackend.user.model.dto.CreateBookmarkRequest;
import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal BlogUserDetails userDetails) {
        return ResponseEntity.ok(
                userService.getById(userDetails.getUserId())
        );
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<PagedResponse<PostCardResponse>> getBookmarkedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                userService.getBookmarksForUser(blogUserDetails.getUserId(), pageable)
        );
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<Void> bookmarkPost(
            @AuthenticationPrincipal BlogUserDetails blogUserDetails,
            @RequestBody CreateBookmarkRequest createBookmarkRequest
    ) {
        userService.createBookmark(createBookmarkRequest.postId(), blogUserDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bookmarks/{postId}")
    public ResponseEntity<Void> unbookmarkPost(
            @AuthenticationPrincipal BlogUserDetails blogUserDetails,
            @PathVariable UUID postId
    ) {
        userService.deleteBookmark(postId, blogUserDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/following")
    public ResponseEntity<Set<UserResponse>> getMyFollowing(@AuthenticationPrincipal BlogUserDetails currentUser) {
        return ResponseEntity.ok(
                userService.getFollowingList(currentUser.getUserId())
        );
    }

    @PostMapping("/following/{targetUserId}")
    public ResponseEntity<Void> followUser(
            @AuthenticationPrincipal BlogUserDetails currentUser,
            @PathVariable UUID targetUserId
    ) {
        userService.followUser(currentUser.getUserId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/following/{targetUserId}")
    public ResponseEntity<Void> unfollowUser(
            @AuthenticationPrincipal BlogUserDetails currentUser,
            @PathVariable UUID targetUserId
    ) {
        userService.unfollowUser(currentUser.getUserId(), targetUserId);
        return ResponseEntity.noContent().build();
    }
}
