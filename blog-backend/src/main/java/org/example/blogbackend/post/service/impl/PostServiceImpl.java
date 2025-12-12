package org.example.blogbackend.post.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.category.repository.CategoryRepository;
import org.example.blogbackend.comment.model.SearchType;
import org.example.blogbackend.post.dto.request.PostRequest;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.dto.response.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.projection.PostCardView;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.shared.mapper.PageMapper;
import org.example.blogbackend.tag.model.dto.request.CreateTagRequest;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.tag.repository.TagRepository;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final int WORDS_PER_MINUTE = 200;
    private static final String POST_NOT_FOUND_MSG = "Post not found with ID: ";
    private static final String USER_NOT_FOUND_MSG = "User not found with ID: ";

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, UUID userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId));

        Post post = postMapper.toEntity(request);
        post.setAuthor(author);

        if (post.getStatus() == null) {
            post.setStatus(PostStatus.DRAFT);
        }

        calculateAndSetReadingTime(post);
        resolveCategory(post, request.category());
        resolveTags(post, request.tags());

        Post savedPost = postRepository.save(post);
        return postMapper.toPostResponse(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostById(UUID postId, UUID userId) {
        Post post = getPostEntity(postId);

        boolean isBookmarked = false;
        boolean isFollowingAuthor = false;
        boolean isLiked = false;

        if (userId != null) {
            isBookmarked = userRepository.isBookmarked(userId, post.getId());
            isFollowingAuthor = userRepository.isFollowing(userId, post.getAuthor().getId());
            isLiked = userRepository.isLiked(userId, post.getId());
        }

        return postMapper.toPostDetailResponse(post, isBookmarked, isFollowingAuthor, isLiked);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getPostCards(
            UUID userId,
            String query,
            SearchType searchType, // 👈 New Parameter
            UUID categoryId,
            UUID tagId,
            Pageable pageable
    ) {
        // Convert Enum to String for JPQL, default to MIXED if null
        String typeString = (searchType != null) ? searchType.name() : SearchType.MIXED.name();

        // 1. Fetch filtered view directly from Repository
        Page<PostCardView> postCards = postRepository.findFilteredPosts(
                PostStatus.PUBLISHED,
                query,
                typeString,
                categoryId,
                tagId,
                pageable
        );

        // 2. Extract IDs to check bookmarks efficiently
        Set<UUID> postIds = postCards.getContent().stream()
                .map(PostCardView::getId)
                .collect(Collectors.toSet());

        Set<UUID> bookmarkedPostIds = (userId != null && !postIds.isEmpty())
                ? userRepository.findBookmarkedPostIdsByUserIdAndPostIdIn(userId, postIds)
                : Collections.emptySet();

        // 3. Map to DTO
        Page<PostCardResponse> responsePage = postCards.map(postCard ->
                postMapper.toPostCardResponse(
                        postCard,
                        bookmarkedPostIds.contains(postCard.getId())
                )
        );

        return pageMapper.toPagedResponse(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCardResponse> getDraftPosts(UUID userId) {
        List<PostCardView> drafts = postRepository.findProjectedByAuthor_IdAndStatus(userId, PostStatus.DRAFT);
        return drafts.stream()
                .map(post -> postMapper.toPostCardResponse(post, false))
                .toList();
    }

    @Override
    @Transactional
    public PostResponse updatePost(UUID postId, PostRequest request, UUID currentUserId) {
        Post post = getPostEntity(postId);
        validateAuthorOwnership(post, currentUserId);

        postMapper.updatePostFromRequest(request, post);
        calculateAndSetReadingTime(post);
        resolveCategory(post, request.category());
        resolveTags(post, request.tags());

        Post updatedPost = postRepository.save(post);
        return postMapper.toPostResponse(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(UUID postId, UUID userId) {
        Post post = getPostEntity(postId);
        validateAuthorOwnership(post, userId);
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public PostResponse toggleLike(UUID postId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG + userId);
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        boolean isLiked = postRepository.existsByPostIdAndUserId(postId, userId);

        if (isLiked) {
            postRepository.removeLike(postId, userId);
            postRepository.decrementLikes(postId);
            post.setLikes(Math.max(0, post.getLikes() - 1));
        } else {
            postRepository.addLike(postId, userId);
            postRepository.incrementLikes(postId);
            post.setLikes(post.getLikes() + 1);
        }
        return postMapper.toPostResponse(post);
    }

    // --- Helper Methods ---

    private Post getPostEntity(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MSG + postId));
    }

    private void validateAuthorOwnership(Post post, UUID userId) {
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("You are not the author of this post");
        }
    }

    private void calculateAndSetReadingTime(Post post) {
        if (post.getContent() == null) {
            post.setReadingTime(0);
            return;
        }
        int wordCount = post.getContent().trim().split("\\s+").length;
        int readingTime = (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
        post.setReadingTime(readingTime);
    }

    private void resolveCategory(Post post, CreateCategoryRequest categoryRequest) {
        String categoryName = categoryRequest.name();
        Category category = categoryRepository.findByNameIgnoreCase(categoryName).orElse(
                Category.builder().name(categoryName).build()
        );
        categoryRepository.save(category);
        post.setCategory(category);
    }

    private void resolveTags(Post post, Set<CreateTagRequest> tagRequests) {
        Set<String> tagNames = tagRequests.stream()
                .map(CreateTagRequest::name)
                .collect(Collectors.toSet());

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            Set<Tag> newTags = tagRepository.findByNameNotIn(tagNames);
            tagRepository.saveAll(newTags);
            post.setTags(newTags);
        }
    }
}
