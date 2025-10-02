package org.example.blogbackend.post.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.post.service.dto.PostWithBookmark;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.category.service.CategoryService;
import org.example.blogbackend.tag.service.TagService;
import org.example.blogbackend.user.repository.UserRepository;
import org.example.blogbackend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final int WORDS_PER_MINUTE = 200;
    private static final String POST_NOT_FOUND_MESSAGE = "Post not found with ID: ";

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserService userService;

    // =====================
    // Public Methods
    // =====================

    @Override
    @Transactional(readOnly = true)
    public PostWithBookmark getPostById(UUID id, UUID userId) {
        Post post = findPostByIdOrThrow(id);
        boolean isBookmarked = userService.getPostBookmarkStatuses(userId, List.of(post.getId())).getFirst();
        return new PostWithBookmark(post, isBookmarked);
    }

    @Override
    public Post getPostById(UUID id) {
        return findPostByIdOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostWithBookmark> getAllPosts(UUID categoryId, UUID tagId, UUID userId) {

        if (categoryId != null && tagId != null) {
            List<Post> posts = findPublishedPostsByCategoryAndTag(categoryId, tagId);
            return attachBookmarks(posts, userId);
        }
        if (categoryId != null) {
            List<Post> posts = findPublishedPostsByCategory(categoryId);
            return attachBookmarks(posts, userId);
        }
        if (tagId != null) {
            List<Post> posts = findPublishedPostsByTag(tagId);
            return attachBookmarks(posts, userId);
        }
        List<Post> posts = findAllPublishedPosts();
        return attachBookmarks(posts, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getDraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }

    @Override
    @Transactional
    public Post createPost(Post post) {
        validatePost(post);
        calculateAndSetReadingTime(post);
        resolveCategory(post);
        resolveTags(post);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(UUID id, Post postUpdate) {
        Post existingPost = findPostByIdOrThrow(id);
        validatePost(postUpdate);
        updatePostContent(existingPost, postUpdate);
        updateCategoryIfChanged(existingPost, postUpdate);
        updateTagsIfChanged(existingPost, postUpdate);
        return postRepository.save(existingPost);
    }

    @Override
    @Transactional
    public void deletePost(UUID id) {
        Post post = findPostByIdOrThrow(id);
        postRepository.delete(post);
    }

    @Override
    public boolean existsPostById(UUID id) {
        return postRepository.existsById(id);
    }

    @Transactional
    public Post toggleLike(UUID postId, User user) {
        Post post = findPostByIdOrThrow(postId);
        if (post.getLikedBy().contains(user)) {
            user.unlikePost(post); // keeps both sides in sync
            post.setLikes(post.getLikes() - 1);
        } else {
            user.likePost(post); // keeps both sides in sync
            post.setLikes(post.getLikes() + 1);
        }
        return postRepository.save(post);
    }

    @Override
    public Post savePost(Post post) {
        postRepository.save(post);
        return post;
    }

    // =====================
    // Private Helper Methods
    // =====================

    private List<PostWithBookmark> attachBookmarks(List<Post> posts, UUID userId) {
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        if (userId == null) {
            return posts.stream()
                    .map(post -> new PostWithBookmark(post, false))
                    .toList();
        }
        List<UUID> postIds = posts.stream().map(Post::getId).toList();
        List<Boolean> bookmarks = userService.getPostBookmarkStatuses(userId, postIds);
        return IntStream.range(0, posts.size())
                .mapToObj(i -> new PostWithBookmark(posts.get(i), bookmarks.get(i)))
                .toList();
    }

    private Post findPostByIdOrThrow(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MESSAGE + id));
    }

    private List<Post> findPublishedPostsByCategoryAndTag(UUID categoryId, UUID tagId) {
        Category category = categoryService.getCategoryById(categoryId);
        Tag tag = tagService.getTagById(tagId);
        return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                PostStatus.PUBLISHED, category, tag);
    }

    private List<Post> findPublishedPostsByCategory(UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, category);
    }

    private List<Post> findPublishedPostsByTag(UUID tagId) {
        Tag tag = tagService.getTagById(tagId);
        return postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED, tag);
    }

    private List<Post> findAllPublishedPosts() {
        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    private void validatePost(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (post.getDescription() == null || post.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Post description cannot be empty");
        }
        if (post.getCategory() == null) {
            throw new IllegalArgumentException("Post category cannot be null");
        }
        if (post.getStatus() == null) {
            throw new IllegalArgumentException("Post status cannot be null");
        }
    }

    private void calculateAndSetReadingTime(Post post) {
        int wordCount = post.getContent().trim().split("\\s+").length;
        int readingTime = (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
        post.setReadingTime(readingTime);
    }

    private void resolveCategory(Post post) {
        Category category = categoryService.findOrCreateCategory(post.getCategory());
        post.setCategory(category);
    }

    private void resolveTags(Post post) {
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            Set<Tag> resolvedTags = tagService.findOrCreateTagsIn(post.getTags());
            post.setTags(resolvedTags);
        }
    }

    private void updatePostContent(Post existingPost, Post updatedPost) {
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setDescription(updatedPost.getDescription());
        existingPost.setStatus(updatedPost.getStatus());
        calculateAndSetReadingTime(existingPost);
    }

    private void updateCategoryIfChanged(Post existingPost, Post updatedPost) {
        Category existingCategory = existingPost.getCategory();
        Category updatedCategory = updatedPost.getCategory();

        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            Category resolvedCategory = categoryService.findOrCreateCategory(updatedCategory);
            existingPost.setCategory(resolvedCategory);
        }
    }

    private void updateTagsIfChanged(Post existingPost, Post updatedPost) {
        Set<Tag> existingTags = existingPost.getTags();
        Set<Tag> updatedTags = updatedPost.getTags();

        if (updatedTags == null || updatedTags.isEmpty()) {
            existingPost.setTags(Set.of());
            return;
        }

        // Compare tags by name instead of ID (newly created tags won’t have an ID yet)
        if (!areTagSetsEqual(existingTags, updatedTags)) {
            Set<Tag> resolvedTags = tagService.findOrCreateTagsIn(updatedTags);
            existingPost.setTags(resolvedTags);
        }
    }

    private boolean areTagSetsEqual(Set<Tag> set1, Set<Tag> set2) {
        if (set1 == null || set2 == null) return false;
        if (set1.size() != set2.size()) return false;

        return set1.stream()
                .map(Tag::getName)
                .allMatch(name -> set2.stream()
                        .anyMatch(tag -> tag.getName().equals(name)));
    }
}
