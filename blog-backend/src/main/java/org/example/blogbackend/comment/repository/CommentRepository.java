package org.example.blogbackend.comment.repository;

import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByPostId(UUID postId);
    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);
    boolean existsByIdAndPostId(UUID id, UUID postId);
}
