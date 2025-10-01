package org.example.blogbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.post.controller.dto.response.PostResponse;
import org.example.blogbackend.post.controller.dto.response.PostWithBookmarkResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostMapper postMapper;

    @GetMapping("/bookmarks")
    public ResponseEntity<List<PostResponse>> getBookmarkedPosts(@AuthenticationPrincipal BlogUserDetails blogUserDetails) {
        User user = userService.getById(blogUserDetails.getUserId());
        List<PostResponse> bookmarks = user.getBookmarkedPosts().stream()
                .map(postMapper::toPostResponse)
                .toList();
        return ResponseEntity.ok(bookmarks);
    }
}
