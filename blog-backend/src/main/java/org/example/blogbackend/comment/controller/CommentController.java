package org.example.blogbackend.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.service.CommentService;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.security.BlogUserDetails;
import org.springframework.data.domain.Pageable;
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
    public UUID create(
            @PathVariable UUID postId,
            @RequestBody String content,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        return commentService.createRoot(postId, blogUserDetails.getUserId(), content);
    }

    @PostMapping("/{commentId}/reply")
    public UUID reply(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @RequestBody String content,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        return commentService.reply(postId, commentId, blogUserDetails.getUserId(), content);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CommentResponse>> getComments(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                commentService.getComments(postId, blogUserDetails.getUserId(), pageable)
        );
    }

    @PostMapping("/{commentId}/like")
    public void like(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        commentService.like(commentId, blogUserDetails.getUserId());
    }

    @DeleteMapping("/{commentId}/like")
    public void unlike(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        commentService.unlike(commentId, blogUserDetails.getUserId());
    }

    @DeleteMapping("/{commentId}")
    public void delete(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails
    ) {
        commentService.delete(postId, commentId, blogUserDetails.getUserId());
    }
}
