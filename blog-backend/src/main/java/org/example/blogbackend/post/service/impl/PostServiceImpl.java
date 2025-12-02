package org.example.blogbackend.post.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.category.repository.CategoryRepository;
import org.example.blogbackend.category.service.CategoryService;
import org.example.blogbackend.post.dto.request.PostRequest;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.dto.response.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.projection.PostCardView;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.tag.model.dto.request.CreateTagRequest;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.tag.repository.TagRepository;
import org.example.blogbackend.tag.service.TagService;
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
    private final TagService tagService;
    private final CategoryService categoryService;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, UUID userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId));

        Post post = postMapper.toEntity(request);
        post.setAuthor(author); // IMPORTANT: Set the author
        post.setStatus(PostStatus.DRAFT); // Default to Draft (optional, depends on logic)

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

        // Safely check logic (assuming if userId is provided, user exists)
        boolean isBookmarked = false;
        boolean isFollowingAuthor = false;

        if (userId != null) {
            isBookmarked = userRepository.isBookmarked(userId, post.getId());
            // Check if current user is following the post author
            isFollowingAuthor = userRepository.isFollowing(userId, post.getAuthor().getId());
        }

        return postMapper.toPostDetailResponse(post, isBookmarked, isFollowingAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostCardResponse> getPostCards(UUID userId, UUID categoryId, UUID tagId, Pageable pageable) {
        Page<PostCardView> postCards = fetchPostCardViews(categoryId, tagId, pageable);

        Set<UUID> postIds = postCards.getContent().stream()
                .map(PostCardView::getId)
                .collect(Collectors.toSet());

        // Batch fetch bookmarks to avoid N+1 problem
        Set<UUID> bookmarkedPostIds = (userId != null && !postIds.isEmpty())
                ? userRepository.findBookmarkedPostIdsByUserIdAndPostIdIn(userId, postIds)
                : Collections.emptySet();

        return postCards.map(postCard ->
                postMapper.toPostCardResponse(
                        postCard,
                        bookmarkedPostIds.contains(postCard.getId())
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCardResponse> getDraftPosts(UUID userId) {

        List<PostCardView> drafts = postRepository.findProjectedByAuthor_IdAndStatus(userId, PostStatus.DRAFT);

        // Convert to DTOs
        return drafts.stream()
                .map(post -> postMapper.toPostCardResponse(post, false)) // Drafts are likely not bookmarked
                .toList();
    }

    @Override
    @Transactional
    public PostResponse updatePost(UUID postId, PostRequest request, UUID currentUserId) {
        Post post = getPostEntity(postId);

        validateAuthorOwnership(post, currentUserId);

        postMapper.updatePostFromRequest(request, post);
        calculateAndSetReadingTime(post); // Re-calculate if content changed
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
        Post post = getPostEntity(postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId));

        // Assuming User entity has helper methods that manage the relationship (both sides)
        if (post.getLikedBy().contains(user)) {
            user.unlikePost(post);
            post.setLikes(Math.max(0, post.getLikes() - 1)); // Prevent negative likes
        } else {
            user.likePost(post);
            post.setLikes(post.getLikes() + 1);
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toPostResponse(savedPost);
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

    private Page<PostCardView> fetchPostCardViews(UUID categoryId, UUID tagId, Pageable pageable) {
        if (categoryId != null && tagId != null) {
            return postRepository.findProjectedByStatusAndCategory_IdAndTags_Id(
                    PostStatus.PUBLISHED, categoryId, tagId, pageable);
        } else if (categoryId != null) {
            return postRepository.findProjectedByStatusAndCategory_Id(
                    PostStatus.PUBLISHED, categoryId, pageable);
        } else if (tagId != null) {
            return postRepository.findProjectedByStatusAndTags_Id(
                    PostStatus.PUBLISHED, tagId, pageable);
        } else {
            return postRepository.findProjectedByStatus(PostStatus.PUBLISHED, pageable);
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
