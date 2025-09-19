package org.example.blogbackend.comment.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.repository.CommentRepository;
import org.example.blogbackend.comment.service.CommentService;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String COMMENT_NOT_FOUND = "Comment with ID %s not found for post %s";
    private static final String POST_NOT_FOUND = "Post with ID %s not found";
    private static final String INVALID_COMMENT_CONTENT = "Comment content cannot be null or empty";
    private static final String INVALID_ID = "ID cannot be null";

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        validateId(postId, "Post ID");
        // Ensure the post exists
        if(!postService.existsPostById(postId)) {
            throw new EntityNotFoundException(String.format(POST_NOT_FOUND, postId));
        }
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Comment getCommentById(UUID postId, UUID commentId) {
        validateId(postId, "Post ID");
        validateId(commentId, "Comment ID");

        return commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(COMMENT_NOT_FOUND, commentId, postId)));
    }

    @Override
    @Transactional
    public Comment createComment(UUID postId, Comment comment) {
        validateId(postId, "Post ID");
        validateComment(comment);

        Post post = postService.getPostById(postId);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID postId, UUID commentId) {
        validateId(postId, "Post ID");
        validateId(commentId, "Comment ID");

        if (!commentRepository.existsByIdAndPostId(commentId, postId)) {
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId, postId));
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public Comment updateComment(UUID postId, UUID commentId, Comment comment) {
        validateId(postId, "Post ID");
        validateId(commentId, "Comment ID");
        validateComment(comment);

        Comment existingComment = getCommentById(postId, commentId);
        existingComment.setContent(comment.getContent());
        // Consider adding other fields like lastModifiedDate if needed

        return commentRepository.save(existingComment);
    }

    private void validateId(UUID id, String idName) {
        if (id == null) {
            throw new IllegalArgumentException(idName + " " + INVALID_ID);
        }
    }

    private void validateComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }

        if (!StringUtils.hasText(comment.getContent())) {
            throw new IllegalArgumentException(INVALID_COMMENT_CONTENT);
        }
    }
}
