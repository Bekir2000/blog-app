package org.example.blogbackend.comment.service;

import org.example.blogbackend.comment.model.entity.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment getCommentById(UUID postId, UUID id);
    Comment createComment(UUID postId, Comment comment);
    void deleteComment(UUID postId, UUID id);
    Comment updateComment(UUID postId, UUID id, Comment comment);
    List<Comment> getCommentsByPostId(UUID postId);
}
