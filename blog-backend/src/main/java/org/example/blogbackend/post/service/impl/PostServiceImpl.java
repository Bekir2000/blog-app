package org.example.blogbackend.post.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.dto.request.PostDraftRequest;
import org.example.blogbackend.post.dto.response.PostDraftResult;
import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.dto.response.card.draft.DraftCardResponse;
import org.example.blogbackend.post.dto.response.detail.PostDetailResponse;
import org.example.blogbackend.post.dto.response.detail.draft.DraftDetailResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.entity.PostLike;
import org.example.blogbackend.post.model.entity.PostVersion;
import org.example.blogbackend.post.model.projection.PostWithDetailsDto;
import org.example.blogbackend.post.repository.PostLikeRepository;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.post.repository.PostVersionRepository;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.mapper.PageMapper;
import org.example.blogbackend.shared.security.InputSanitizer;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.UserRepository;
import org.owasp.html.PolicyFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostVersionRepository postVersionRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    // Security dependencies
    private final PolicyFactory plainTextPolicy;
    private final List<String> allowedImageHosts;
    private final InputSanitizer inputSanitizer;

    private final PostMapper postMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public PostDraftResult createPost(UUID authorId, PostDraftRequest req) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + authorId));

        // 1. Sanitize text and Validate URL
        String cleanTitle = inputSanitizer.sanitizeText(req.title());
        String cleanContent = inputSanitizer.sanitizeText(req.content());
        Set<String> cleanTags = inputSanitizer.sanitizeTags(req.tags());
        String cleanImageUrl = inputSanitizer.sanitizeUrl(req.imageUrl());

        // Create Entity
        Post post = Post.create(author);
        PostVersion draft = PostVersion.createDraft(post, cleanTitle, cleanContent, cleanImageUrl);

        draft.createDescription(cleanContent);
        draft.setCategory(req.category());
        draft.setTags(cleanTags);
        post.addVersion(draft);

        postRepository.save(post);
        return new PostDraftResult(post.getId(), draft.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getPostCards(
            UUID userId,
            String authorName,
            Category category,
            String title,
            String tag,
            Pageable pageable
    ) {
        return pageMapper.toPagedResponse(
                postRepository
                        .findPublishedPosts(userId, title, category, tag, authorName, pageable)
                        .map(postMapper::toPostCardResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostById(UUID postId, UUID userId) {
        PostWithDetailsDto postWithDetailsDto = postRepository.findPostWithDetails(postId, userId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postId)
        );
        return postMapper.toPostDetailResponse(postWithDetailsDto);
    }

    @Override
    @Transactional
    public PostDraftResult saveExistingDraft(
            UUID postId,
            UUID draftId,
            UUID userId,
            PostDraftRequest req) {

        PostVersion draft = postVersionRepository.findById(draftId).orElseThrow(
                () -> new EntityNotFoundException("Draft not found with id: " + draftId)
        );

        if (!draft.belongsToPost(postId)) {
            throw new IllegalArgumentException("Draft does not belong to the specified post ID");
        }

        if (!draft.getPost().isAuthor(userId)) {
            throw new AccessDeniedException("You are not the author of this post");
        }

        // 1. Sanitize text and Validate URL
        String cleanTitle = inputSanitizer.sanitizeText(req.title());
        String cleanContent = inputSanitizer.sanitizeText(req.content());
        Set<String> cleanTags = inputSanitizer.sanitizeTags(req.tags());
        String cleanImageUrl = inputSanitizer.sanitizeUrl(req.imageUrl());

        // 2. Update Entity
        draft.update(cleanTitle, cleanContent, cleanImageUrl);
        draft.setCategory(req.category());
        draft.setTags(cleanTags);
        draft.createDescription(cleanContent);

        postVersionRepository.save(draft);

        return new PostDraftResult(postId, draftId);
    }

    @Override
    @Transactional
    public PostDraftResult addNewDraft(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!post.isAuthor(userId)) {
            throw new AccessDeniedException("You are not the author of this article");
        }

        PostVersion draft = post.createDraftFromPublishedVersion();
        postVersionRepository.save(draft);
        return new PostDraftResult(post.getId(), draft.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DraftCardResponse> getDrafts(UUID userId, Pageable pageable) {
        return pageMapper.toPagedResponse(
                postVersionRepository.findDraftsByUserId(userId, pageable)
                        .map(postMapper::toDraftCardResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DraftDetailResponse getDraftById(UUID postId, UUID draftId, UUID userId) {
        PostVersion draft = postVersionRepository.findById(draftId).orElseThrow(
                () -> new EntityNotFoundException("Draft not found with id: " + draftId)
        );

        if (!draft.getPost().isAuthor(userId)) {
            throw new AccessDeniedException("You are not the author of this article");
        }

        if (!draft.belongsToPost(postId)) {
            throw new IllegalArgumentException("Draft does not belong to the specified post ID");
        }

        if (!draft.isDraft()) {
            throw new IllegalStateException("No Draft");
        }

        return postMapper.toDraftDetailResponse(draft);
    }

    @Override
    @Transactional
    public void publish(UUID userId, UUID postId, UUID draftId) {
        PostVersion draft = postVersionRepository.findById(draftId).orElseThrow(
                () -> new EntityNotFoundException("Draft not found with id: " + draftId)
        );

        if (!draft.belongsToPost(postId)) {
            throw new IllegalArgumentException("Draft does not belong to the post specified in the URL");
        }

        Post post = draft.getPost();
        if (!post.isAuthor(userId)) {
            throw new AccessDeniedException("You are not the author of this post");
        }

        if (!draft.isDraft()) {
            throw new IllegalStateException("Version is already published or archived");
        }

        // Logic pushed to Entity: validation + reading time + version swapping
        UUID oldPublishedVersionId = post.publishVersion(draft);

        if (oldPublishedVersionId != null) {
            postVersionRepository.deleteById(oldPublishedVersionId);
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(UUID userId, UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postId)
        );

        if (!post.isAuthor(userId)) {
            throw new AccessDeniedException("You are not the author of this post");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void likePost(UUID postId, UUID userId) {
        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        Post postProxy = postRepository.getReferenceById(postId);

        if (!isLiked) {
            PostLike newLike = PostLike.create(postProxy, userId);
            postLikeRepository.save(newLike);
            postRepository.incrementLikeCount(postId);
        }
    }

    @Override
    @Transactional
    public void unlikePost(UUID postId, UUID userId) {
        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (isLiked) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            postRepository.decrementLikeCount(postId);
        }
    }

    @Override
    @Transactional
    public void deleteDraft(UUID userId, UUID postId, UUID draftId) {
        PostVersion draft = postVersionRepository.findById(draftId).orElseThrow(
                () -> new EntityNotFoundException("Draft not found with id: " + draftId)
        );

        if (!draft.belongsToPost(postId)) {
            throw new IllegalArgumentException("Draft does not belong to the specified post ID");
        }

        if (!draft.getPost().isAuthor(userId)) {
            throw new AccessDeniedException("You are not the author of this post");
        }

        if (!draft.isDraft()) {
            throw new IllegalStateException("Cannot delete published version");
        }

        postVersionRepository.delete(draft);
    }
}
