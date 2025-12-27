package org.example.blogbackend.comment.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.comment.model.entity.CommentLike;
import org.example.blogbackend.comment.model.mapper.CommentMapper;
import org.example.blogbackend.comment.model.projection.CommentWithDetails;
import org.example.blogbackend.comment.repository.CommentLikeRepository;
import org.example.blogbackend.comment.repository.CommentRepository;
import org.example.blogbackend.comment.service.CommentService;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.mapper.PageMapper;
import org.example.blogbackend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final int MAX_DEPTH = 2;

    private final CommentRepository commentRepo;
    private final CommentLikeRepository likeRepo;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final PageMapper pageMapper;
    private final CommentMapper commentMapper;

    public UUID createRoot(UUID postId, UUID userId, String content) {
        boolean isUserExists = userRepository.existsById(userId);
        if (!isUserExists) {
            throw new AccessDeniedException("User does not exist");
        }

        if (content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }
        Comment root = Comment.createRoot(postId, userId, content);
        commentRepo.incrementReplyCount(root.getId());
        postRepository.incrementCommentCount(postId);
        return commentRepo.save(root).getId();
    }

    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getComments(
            UUID postId,
            UUID userId,
            Pageable pageable
    ) {
        Page<CommentWithDetails> roots =
                commentRepo.findRootComments(postId, userId, pageable);

        List<CommentWithDetails> replies =
                commentRepo.findAllReplies(postId, userId);

        // 1) map ALL comments to DTOs
        Map<UUID, CommentResponse> dtoMap = new HashMap<>();

        roots.forEach(c ->
                dtoMap.put(c.comment().getId(), commentMapper.toCommentResponse(c))
        );

        replies.forEach(c ->
                dtoMap.put(c.comment().getId(), commentMapper.toCommentResponse(c))
        );

        // 2) attach replies
        replies.forEach(c -> {
            Comment parent = c.comment().getParent();
            if (parent != null) {
                CommentResponse parentDto = dtoMap.get(parent.getId());
                CommentResponse childDto  = dtoMap.get(c.comment().getId());

                if (parentDto != null) {
                    parentDto.getReplies().add(childDto);
                }
            }
        });

        // 3) page result
        Page<CommentResponse> page = roots.map(
                c -> dtoMap.get(c.comment().getId())
        );

        return pageMapper.toPagedResponse(page);
    }


    public UUID reply(UUID postId, UUID parentId, UUID userId, String content) {
        boolean isUserExists = userRepository.existsById(userId);
        if (!isUserExists) {
            throw new AccessDeniedException("User does not exist");
        }

        if (content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }

        Comment root = commentRepo.findByIdAndPostId(parentId, postId)
                .orElseThrow();

        if (root.getReplyCount() >= MAX_DEPTH) {
            throw new IllegalStateException("Max depth reached");
        }

        Comment reply = Comment.createReply(postId, userId, root, content);

        if (!reply.belongsToPost(postId)){
            throw new IllegalStateException("Comment does not belong to post");
        }

        root.addReply(reply);
        commentRepo.incrementReplyCount(root.getId());
        postRepository.incrementCommentCount(postId);
        commentRepo.save(root);

        return reply.getId();
    }

    public void delete(UUID postId, UUID commentId, UUID userId) {
        Comment c = commentRepo.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment not found")
        );

        if (!c.isAuthor(userId)) {
            throw new AccessDeniedException("Not owner");
        }

        if (!c.belongsToPost(postId)){
            throw new IllegalStateException("Comment does not belong to post");
        }

        Comment root = c.getParent();
        commentRepo.decrementReplyCount(root.getId());
        postRepository.decrementCommentCount(postId);
        commentRepo.delete(c);
    }

    public void like(UUID commentId, UUID userId) {
        boolean isUserExists = userRepository.existsById(userId);
        if (!isUserExists) {
            throw new AccessDeniedException("User does not exist");
        }

        Comment c = commentRepo.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment not found")
        );
        if (c.isDeleted()) return;

        if (likeRepo.existsByCommentIdAndUserId(commentId, userId)) return;

        likeRepo.save(new CommentLike(commentId, userId));
        commentRepo.incrementLike(commentId);
    }

    public void unlike(UUID commentId, UUID userId) {
        boolean isUserExists = userRepository.existsById(userId);
        if (!isUserExists) {
            throw new AccessDeniedException("User does not exist");
        }

        if (!likeRepo.existsByCommentIdAndUserId(commentId, userId)) return;

        likeRepo.deleteByCommentIdAndUserId(commentId, userId);
        commentRepo.decrementLike(commentId);
    }
}
