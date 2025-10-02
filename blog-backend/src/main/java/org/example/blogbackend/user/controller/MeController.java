package org.example.blogbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.post.controller.dto.response.PostResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.user.mapper.UserMapper;
import org.example.blogbackend.user.model.dto.CreateBookmarkRequest;
import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal BlogUserDetails userDetails) {
        User user = userService.getById(userDetails.getUserId());
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<List<PostResponse>> getBookmarkedPosts(@AuthenticationPrincipal BlogUserDetails blogUserDetails) {
        User user = userService.getById(blogUserDetails.getUserId());
        return ResponseEntity.ok(
                user.getBookmarkedPosts().stream()
                        .map(postMapper::toPostResponse)
                        .toList()
        );
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<Void> bookmarkPost(@AuthenticationPrincipal BlogUserDetails blogUserDetails, @RequestBody CreateBookmarkRequest createBookmarkRequest) {
        userService.createBookmark(createBookmarkRequest.postId(), blogUserDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bookmarks/{postId}")
    public ResponseEntity<Void> unbookmarkPost(@AuthenticationPrincipal BlogUserDetails blogUserDetails, @PathVariable UUID postId) {
        userService.deleteBookmark(postId, blogUserDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

}
