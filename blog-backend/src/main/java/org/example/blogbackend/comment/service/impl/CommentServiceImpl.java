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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String COMMENT_NOT_FOUND = "Comment with ID %s not found";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper; // 👈 Now we actually use this!
    private final PageMapper pageMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getCommentsByPostId(UUID postId, UUID userId, Pageable pageable) {
        Page<Comment> rootPage = commentRepository.findRootComments(postId, pageable);
        List<Comment> rootComments = rootPage.getContent();

        // Check likes for visible comments
        Set<UUID> likedCommentIds = new HashSet<>();
        if (userId != null && !rootComments.isEmpty()) {
            likedCommentIds = commentRepository.findLikedCommentIds(rootComments, userId);
        }

        final Set<UUID> finalLikedIds = likedCommentIds;
        List<CommentResponse> responseList = rootComments.stream()
                .map(c -> mapToResponseRecursive(c, finalLikedIds))
                .toList();

        return pageMapper.toPagedResponse(new PageImpl<>(responseList, pageable, rootPage.getTotalElements()));
    }

    // --- Recursive Mapper using MapStruct ---
    private CommentResponse mapToResponseRecursive(Comment comment, Set<UUID> likedIds) {
        boolean isLiked = likedIds.contains(comment.getId());

        // 1. Recursively map replies
        List<CommentResponse> replies = comment.getReplies().stream()
                .map(child -> mapToResponseRecursive(child, likedIds))
                .toList();

        // 2. Delegate to MapStruct for the DTO creation
        return commentMapper.toTreeResponse(comment, isLiked, replies);
    }

    // ... createComment, toggleLike, etc. (No changes needed) ...

    @Override
    @Transactional
    public CommentResponse createComment(UUID postId, UUID userId, CreateCommentRequest request) {
        Post post = postRepository.getReferenceById(postId);
        User author = userRepository.getReferenceById(userId);

        Comment comment = commentMapper.toComment(request);
        comment.setPost(post);
        comment.setAuthor(author);

        if (request.parentCommentId() != null) {
            if (!commentRepository.existsById(request.parentCommentId())) {
                throw new EntityNotFoundException("Parent comment not found");
            }
            comment.setParent(commentRepository.getReferenceById(request.parentCommentId()));
        }

        Comment savedComment = commentRepository.save(comment);
        postRepository.incrementCommentsCount(postId);

        return commentMapper.toCommentResponse(savedComment, false);
    }

    @Override
    @Transactional
    public void toggleLike(UUID commentId, UUID userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId));
        }
        if (commentRepository.existsLikeByCommentIdAndUserId(commentId, userId)) {
            commentRepository.removeLike(commentId, userId);
        } else {
            commentRepository.addLike(commentId, userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID postId, UUID commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));
        return mapToResponseRecursive(comment, Collections.emptySet());
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID postId, UUID commentId, UUID userId, CreateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));
        validateOwnership(comment, userId);
        comment.setContent(request.content());
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
