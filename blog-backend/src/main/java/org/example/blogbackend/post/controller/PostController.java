package org.example.blogbackend.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.model.SearchType;
import org.example.blogbackend.post.dto.request.PostDraftRequest;
import org.example.blogbackend.shared.security.BlogUserDetails;
import org.example.blogbackend.post.dto.request.PostRequest;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.dto.response.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostResponse;
import org.example.blogbackend.post.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostById(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        UUID userId = (userDetails != null) ? userDetails.getUserId() : null;
        return ResponseEntity.ok(postService.getPostById(postId, userId));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<PostCardResponse>> getAllPostCards(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "MIXED") SearchType searchType,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        UUID userId = (userDetails != null) ? userDetails.getUserId() : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                postService.getPostCards(userId, query, searchType, categoryId, tagId, pageable)
        );
    }


    @GetMapping("/drafts")
    public ResponseEntity<PagedResponse<PostCardResponse>> getDrafts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        // Sort drafts by newest first
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                postService.getDraftPosts(userDetails.getUserId(), pageable)
        );
    }

    @GetMapping("/published")
    public ResponseEntity<PagedResponse<PostCardResponse>> getMyPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                postService.getUserPublishedPosts(userDetails.getUserId(), pageable)
        );
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request, userDetails.getUserId()));
    }

    @PostMapping("/draft")
    public ResponseEntity<PostResponse> createPostDraft(
            @Valid @RequestBody PostDraftRequest request,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPostDraft(request, userDetails.getUserId()));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.ok(
                postService.updatePost(postId, request, userDetails.getUserId())
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.ok(
                postService.toggleLike(postId, userDetails.getUserId())
        );
    }
}
