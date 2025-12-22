package org.example.blogbackend.comment.service;

import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    UUID createRoot(UUID postId, UUID userId, String content);
    PagedResponse<CommentResponse> getComments(UUID postId, UUID userId, Pageable pageable);
    UUID reply(UUID postId, UUID parentId, UUID userId, String content);
    void like(UUID commentId, UUID userId);
    void unlike(UUID commentId, UUID userId);
    void delete(UUID postId, UUID commentId, UUID userId);
}
