package org.example.blogbackend.post.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.category.repository.CategoryRepository;
import org.example.blogbackend.comment.model.SearchType;
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
import org.example.blogbackend.shared.dto.PagedResponse;
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
import java.util.HashSet;
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

    // =========================================================================
    // WRITE OPERATIONS (Create, Update, Delete)
    // =========================================================================

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, UUID userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId));

        // Map unified DTO to Entity
        Post post = postMapper.toEntity(request);
        post.setAuthor(author);

        // Fallback status if missing (though DTO validation should catch this)
        if (post.getStatus() == null) {
            post.setStatus(PostStatus.DRAFT);
        }

        // Logic Helpers
        calculateAndSetReadingTime(post);
        resolveCategory(post, request.category());
        resolveTags(post, request.tags());

        Post savedPost = postRepository.save(post);
        return postMapper.toPostResponse(savedPost);
    }


    @Override
    @Transactional
    public PostResponse createRevision(UUID originalPostId, UUID userId) {
        Post original = getPostEntity(originalPostId);
        validateAuthorOwnership(original, userId);

        if (original.getStatus() != PostStatus.PUBLISHED) {
            throw new IllegalArgumentException("You can only create revisions of PUBLISHED posts.");
        }

        // Check if a draft already exists for this post to prevent duplicates
        // (Optional logic, depends on preference. Here we just create a new one.)

        // Clone the data into a new Draft entity
        Post draft = Post.builder()
                .title(original.getTitle())
                .content(original.getContent())
                .description(original.getDescription())
                .imageUrl(original.getImageUrl())
                .status(PostStatus.DRAFT) // Force status to DRAFT
                .parentPost(original)     // ✅ Link to Parent
                .author(original.getAuthor())
                .category(original.getCategory())
                .readingTime(original.getReadingTime())
                .tags(new HashSet<>(original.getTags())) // Copy tags
                .build();

        Post savedDraft = postRepository.save(draft);
        return postMapper.toPostResponse(savedDraft);
    }

    // =========================================================================
    // 2. MERGE: Publish the Draft -> Overwrite the Parent
    // =========================================================================
    @Override
    @Transactional
    public PostResponse updatePost(UUID postId, PostRequest request, UUID userId) {
        Post currentPost = getPostEntity(postId);
        validateAuthorOwnership(currentPost, userId);

        // CASE A: MERGING (Publishing a Revision)
        // We are updating a Draft that has a Parent, and the user wants to PUBLISH it.
        if (currentPost.getParentPost() != null && request.status() == PostStatus.PUBLISHED) {

            Post livePost = currentPost.getParentPost();

            // 1. Copy new content from Request -> Live Post
            postMapper.updatePostFromRequest(request, livePost);

            // 2. Re-calculate metadata for Live Post
            calculateAndSetReadingTime(livePost);
            resolveCategory(livePost, request.category());
            resolveTags(livePost, request.tags());

            // 3. Save the Live Post (It keeps its original ID, Likes, and Comments!)
            Post updatedLivePost = postRepository.save(livePost);

            // 4. Delete the temporary Draft
            postRepository.delete(currentPost);

            return postMapper.toPostResponse(updatedLivePost);
        }

        // CASE B: NORMAL UPDATE (Editing a normal draft or live post directly)
        postMapper.updatePostFromRequest(request, currentPost);
        calculateAndSetReadingTime(currentPost);
        resolveCategory(currentPost, request.category());
        resolveTags(currentPost, request.tags());

        Post updatedPost = postRepository.save(currentPost);
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
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MSG + postId));

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

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================

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

    /**
     * 1. Public Feed: Fetches PUBLISHED posts from ANY author
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getPostCards(
            UUID userId, String query, SearchType searchType,
            UUID categoryId, UUID tagId, Pageable pageable
    ) {
        return fetchAndMapPosts(
                userId,          // Viewer
                null,            // AuthorFilter (Null = Any)
                PostStatus.PUBLISHED,
                query, searchType, categoryId, tagId, pageable
        );
    }

    /**
     * 2. Drafts: Fetches DRAFT posts from CURRENT USER only
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getDraftPosts(UUID userId, Pageable pageable) {
        return fetchAndMapPosts(
                userId,          // Viewer
                userId,          // AuthorFilter (Me)
                PostStatus.DRAFT,
                null, null, null, null, // No search/filters for drafts list
                pageable
        );
    }

    /**
     * 3. My Published: Fetches PUBLISHED posts from CURRENT USER only
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getUserPublishedPosts(UUID userId, Pageable pageable) {
        return fetchAndMapPosts(
                userId,          // Viewer
                userId,          // AuthorFilter (Me)
                PostStatus.PUBLISHED,
                null, null, null, null,
                pageable
        );
    }

    // =========================================================================
    // SHARED PRIVATE ENGINE
    // =========================================================================

    private PagedResponse<PostCardResponse> fetchAndMapPosts(
            UUID viewerId,
            UUID authorFilterId,
            PostStatus status,
            String query,
            SearchType searchType,
            UUID categoryId,
            UUID tagId,
            Pageable pageable
    ) {
        String typeString = (searchType != null) ? searchType.name() : SearchType.MIXED.name();

        Page<PostCardView> postCards = postRepository.findFilteredPosts(
                status,
                query,
                authorFilterId,
                typeString,
                categoryId,
                tagId,
                pageable
        );

        // Bulk fetch bookmarks for efficiency
        Set<UUID> postIds = postCards.getContent().stream()
                .map(PostCardView::getId)
                .collect(Collectors.toSet());

        Set<UUID> bookmarkedPostIds = (viewerId != null && !postIds.isEmpty())
                ? userRepository.findBookmarkedPostIdsByUserIdAndPostIdIn(viewerId, postIds)
                : Collections.emptySet();

        Page<PostCardResponse> responsePage = postCards.map(postCard ->
                postMapper.toPostCardResponse(
                        postCard,
                        bookmarkedPostIds.contains(postCard.getId())
                )
        );

        return pageMapper.toPagedResponse(responsePage);
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

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
        // If draft, category can be optional. If publishing, @Valid handles it.
        // If logic reaches here with null, we just skip setting it or set a default.
        if (categoryRequest == null || categoryRequest.name() == null) {
            return;
        }
        String categoryName = categoryRequest.name();
        Category category = categoryRepository.findByNameIgnoreCase(categoryName).orElse(
                Category.builder().name(categoryName).build()
        );
        categoryRepository.save(category);
        post.setCategory(category);
    }

    private void resolveTags(Post post, Set<CreateTagRequest> tagRequests) {
        if (tagRequests == null || tagRequests.isEmpty()) {
            return;
        }
        Set<String> tagNames = tagRequests.stream()
                .map(CreateTagRequest::name)
                .collect(Collectors.toSet());

        if (!tagNames.isEmpty()) {
            Set<Tag> newTags = tagRepository.findByNameNotIn(tagNames);
            tagRepository.saveAll(newTags);
            post.setTags(newTags);
        }
    }
}
