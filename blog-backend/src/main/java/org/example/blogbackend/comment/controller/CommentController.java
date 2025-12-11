package org.example.blogbackend.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.service.CommentService;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.security.BlogUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                commentService.createComment(postId, userDetails.getUserId(), createCommentRequest)
        );
    }

    @GetMapping(path = "/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable UUID postId,
            @PathVariable UUID commentId) {

        return ResponseEntity.ok(
                commentService.getCommentById(postId, commentId)
        );
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CommentResponse>> getAllComments(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // 👇 Capture the user (might be null if user is a guest)
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Handle Guest vs Logged-in logic here (extract ID safely)
        UUID userId = (userDetails != null) ? userDetails.getUserId() : null;

        return ResponseEntity.ok(
                commentService.getCommentsByPostId(postId, userId, pageable)
        );
    }

    @PutMapping(path = "/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.ok(
                commentService.updateComment(
                        postId,
                        commentId,
                        userDetails.getUserId(),
                        createCommentRequest
                )
        );
    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID postId,
                                              @PathVariable UUID commentId,
                                              @AuthenticationPrincipal BlogUserDetails userDetails) {

        commentService.deleteComment(postId, commentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{commentId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        commentService.toggleLike(commentId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
