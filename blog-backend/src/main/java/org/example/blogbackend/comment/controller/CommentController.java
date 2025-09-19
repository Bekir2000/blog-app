package org.example.blogbackend.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.service.CommentService;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.comment.model.mapper.CommentMapper;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable UUID postId) {

        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<CommentResponse> responses = comments.stream()
                                                  .map(commentMapper::toDto)
                                                  .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping(path = "/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable UUID postId,
                                                          @PathVariable UUID commentId) {

        Comment comment = commentService.getCommentById(postId, commentId);
        CommentResponse response = commentMapper.toDto(comment);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        Comment comment = commentMapper.toEntity(createCommentRequest);
        User author = userService.getById(userDetails.getUserId());
        comment.setAuthor(author);
        Comment createdComment = commentService.createComment(postId, comment);
        CommentResponse response = commentMapper.toDto(createdComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = "/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest) {

        Comment comment = commentMapper.toEntity(createCommentRequest);

        Comment updateComment = commentService.updateComment(postId, commentId, comment);
        CommentResponse response = commentMapper.toDto(updateComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID postId,
                                              @PathVariable UUID commentId) {

        commentService.deleteComment(postId, commentId);

        return ResponseEntity.noContent().build();
    }
}
