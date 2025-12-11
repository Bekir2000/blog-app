package org.example.blogbackend.comment.service;

import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(UUID postId, UUID userId, CreateCommentRequest createCommentRequest);

    void toggleLike(UUID commentId, UUID userId);

    CommentResponse getCommentById(UUID postId, UUID id);

    // 👇 CHANGED: Added 'UUID userId' so we can check if the user liked the comments
    PagedResponse<CommentResponse> getCommentsByPostId(UUID postId, UUID userId, Pageable pageable);

    CommentResponse updateComment(UUID postId, UUID commentId, UUID userId, CreateCommentRequest request);

    void deleteComment(UUID postId, UUID commentId, UUID userId);
}
