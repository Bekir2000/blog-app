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

        // --- UNLIMITED NESTING LOGIC ---
        if (request.parentCommentId() != null) {
            // 1. We just need to ensure the parent exists.
            // Using getReferenceById is faster (no DB select), but if the ID is invalid,
            // it will crash on save.
            // Better to use existsById for a polite error, OR just findById if you want to be safe.
            if (!commentRepository.existsById(request.parentCommentId())) {
                throw new EntityNotFoundException("Parent comment not found");
            }

            // 2. Link the comment to its parent
            Comment parentProxy = commentRepository.getReferenceById(request.parentCommentId());
            comment.setParent(parentProxy);
        }
        // -------------------------------

        Comment savedComment = commentRepository.save(comment);
        postRepository.incrementCommentsCount(postId);

        return commentMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getCommentsByPostId(UUID postId, Pageable pageable) {
        // This query fetches the "Roots" (Level 0).
        // The Mapper will then call ".getReplies()".
        // Because of @BatchSize(size=20) in your Entity:
        // - Hibernate will fetch Level 1 for ALL loaded roots in 1 query.
        // - Then Level 2 in 1 query... and so on.
        Page<Comment> commentPage = commentRepository.findAllRootCommentsByPostId(postId, pageable);

        return pageMapper.toPagedResponse(commentPage.map(commentMapper::toCommentResponse));
    }

    // ... (rest of the methods: toggleLike, update, delete remain exactly the same)
    @Override
    @Transactional
    public void toggleLike(UUID commentId, UUID userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId));
        }
        if (commentRepository.existsLikeByCommentIdAndUserId(commentId, userId)) {
            commentRepository.removeLike(commentId, userId);
            commentRepository.decrementLikes(commentId);
        } else {
            commentRepository.addLike(commentId, userId);
            commentRepository.incrementLikes(commentId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID postId, UUID commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID postId, UUID commentId, UUID userId, CreateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));

        validateOwnership(comment, userId);
        comment.setContent(request.content());
        return commentMapper.toCommentResponse(comment);
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
