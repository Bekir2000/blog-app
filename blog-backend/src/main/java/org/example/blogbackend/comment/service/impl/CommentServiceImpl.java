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

    private static final String COMMENT_NOT_FOUND = "Comment with ID %s not found";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public CommentResponse createComment(UUID postId, UUID userId, CreateCommentRequest request) {
        Post post = postRepository.getReferenceById(postId);
        User author = userRepository.getReferenceById(userId);

        Comment comment = commentMapper.toComment(request);
        comment.setPost(post);
        comment.setAuthor(author);

        // --- PARENT/CHILD LOGIC ---
        if (request.parentCommentId() != null) {
            if (!commentRepository.existsById(request.parentCommentId())) {
                throw new EntityNotFoundException("Parent comment not found");
            }
            Comment parentProxy = commentRepository.getReferenceById(request.parentCommentId());
            comment.setParent(parentProxy);
        }
        // --------------------------

        Comment savedComment = commentRepository.save(comment);
        postRepository.incrementCommentsCount(postId);

        // A new comment is never 'liked' by the creator initially
        return commentMapper.toCommentResponse(savedComment, false);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getCommentsByPostId(UUID postId, UUID userId, Pageable pageable) {
        Page<CommentResponse> responsePage;

        if (userId != null) {
            // 1. Logged-in User: Load User Entity so we can check "MEMBER OF"
            // We use findById because we need the actual entity class for the query
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Runs the optimized query with "isLiked = calculated"
            responsePage = commentRepository.findRootCommentsForUser(postId, currentUser, pageable);
        } else {
            // 2. Guest: Runs the optimized query with "isLiked = false"
            responsePage = commentRepository.findRootCommentsForGuest(postId, pageable);
        }

        // 3. Return directly (No mapping needed! The Repository returned DTOs)
        return pageMapper.toPagedResponse(responsePage);
    }

    @Override
    @Transactional
    public void toggleLike(UUID commentId, UUID userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId));
        }

        // Use Native Queries for speed
        if (commentRepository.existsLikeByCommentIdAndUserId(commentId, userId)) {
            commentRepository.removeLike(commentId, userId);
            // NOTE: We do NOT manually decrement 'likesCount' column anymore.
            // The read query calculates it dynamically via SIZE().
        } else {
            commentRepository.addLike(commentId, userId);
            // NOTE: We do NOT manually increment 'likesCount' column anymore.
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID postId, UUID commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));

        // Note: For single fetching, we default isLiked to false for simplicity here.
        // If you need it accurate, use 'commentRepository.existsLikeByCommentIdAndUserId'
        return commentMapper.toCommentResponse(comment, false);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID postId, UUID commentId, UUID userId, CreateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));

        validateOwnership(comment, userId);
        comment.setContent(request.content());

        // Assuming user updating their own comment implies they haven't "liked" it in this context,
        // or passing 'false' is acceptable since the UI optimistically updates.
        // For strict correctness: boolean isLiked = commentRepository.existsLikeByCommentIdAndUserId(commentId, userId);
        return commentMapper.toCommentResponse(comment, false);
    }

    @Override
    @Transactional
    public void deleteComment(UUID postId, UUID commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));

        validateOwnership(comment, userId);
        commentRepository.delete(comment);
        postRepository.decrementCommentsCount(postId);
    }

    private void validateOwnership(Comment comment, UUID userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to modify this comment");
        }
    }
}
