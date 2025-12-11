package org.example.blogbackend.comment.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.comment.model.mapper.CommentMapper;
import org.example.blogbackend.comment.repository.CommentRepository;
import org.example.blogbackend.comment.service.CommentService;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.mapper.PageMapper;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String COMMENT_NOT_FOUND = "Comment with ID %s not found for post %s";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public CommentResponse createComment(UUID postId, UUID userId, CreateCommentRequest request) {
        // Efficient: creates a reference proxy without a DB hit
        Post post = postRepository.getReferenceById(postId);
        User author = userRepository.getReferenceById(userId);

        Comment comment = commentMapper.toComment(request);
        comment.setPost(post);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);

        // Ensure this method uses a custom @Modifying @Query in the Repo for atomicity
        postRepository.incrementCommentsCount(postId);

        return commentMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true) // Performance: optimizations for read-only
    public PagedResponse<CommentResponse> getCommentsByPostId(UUID postId, Pageable pageable) {
        // Optimization: Removed 'postRepository.existsById(postId)'.
        // If the post doesn't exist, we just return an empty page of comments.
        // This saves 1 database round-trip per request.

        // Critical: Ensure commentRepository.findAllByPostId uses "JOIN FETCH c.author"
        // to avoid the N+1 Select problem.
        Page<Comment> commentPage = commentRepository.findAllByPostId(postId, pageable);

        return pageMapper.toPagedResponse(commentPage.map(commentMapper::toCommentResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID postId, UUID commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(COMMENT_NOT_FOUND, commentId, postId)));

        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID postId, UUID commentId, UUID userId, CreateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(COMMENT_NOT_FOUND, commentId, postId)));

        // Security: Check ownership
        validateOwnership(comment, userId);

        // Logic: Directly update the entity.
        // Hibernate "Dirty Checking" will automatically save changes at the end of the transaction.
        comment.setContent(request.content());

        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID postId, UUID commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(COMMENT_NOT_FOUND, commentId, postId)));

        // Security: Check ownership
        validateOwnership(comment, userId);

        commentRepository.delete(comment);

        // Ensure this uses @Modifying in Repo
        postRepository.decrementCommentsCount(postId);
    }

    /**
     * Helper method to ensure only the author can modify their comment.
     */
    private void validateOwnership(Comment comment, UUID userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to modify this comment");
        }
    }
}
