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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public UUID createComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        return commentService.createRoot(postId, blogUserDetails.getUserId(), request.content());
    }

    @PostMapping("/{commentId}/reply")
    public UUID replyComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        return commentService.reply(postId, commentId, blogUserDetails.getUserId(), request.content());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CommentResponse>> getComments(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                commentService.getComments(postId, blogUserDetails.getUserId(), pageable)
        );
    }

    @PostMapping("/{commentId}/like")
    public void likeComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        commentService.like(commentId, blogUserDetails.getUserId());
    }

    @DeleteMapping("/{commentId}/like")
    public void unlikeComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        commentService.unlike(commentId, blogUserDetails.getUserId());
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        commentService.delete(postId, commentId, blogUserDetails.getUserId());
    }
}
