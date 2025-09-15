package org.example.blogbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.model.entities.Category;
import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.model.entities.Tag;
import org.example.blogbackend.model.entities.User;
import org.example.blogbackend.model.enums.PostStatus;
import org.example.blogbackend.repository.PostRepository;
import org.example.blogbackend.service.CategoryService;
import org.example.blogbackend.service.PostService;
import org.example.blogbackend.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    private static final int WORDS_PER_MINUTE = 200;
    private static final String POST_NOT_FOUND_MESSAGE = "Post not found with ID: ";

    // =====================
    // Public CRUD Operations
    // =====================

    // --- Read ---

    @Override
    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MESSAGE + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if (categoryId != null && tagId != null) {
            return findPostsByCategoryAndTag(categoryId, tagId);
        }
        if (categoryId != null) {
            return findPostsByCategory(categoryId);
        }
        if (tagId != null) {
            return findPostsByTag(tagId);
        }
        return findAllPublishedPosts();
    }

    @Override
    public List<Post> getDraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }

    // --- Write ---

    @Override
    @Transactional
    public Post createPost(Post postToCreate) {
        validatePost(postToCreate);
        calculateAndSetReadingTime(postToCreate);
        return postRepository.save(postToCreate);
    }

    @Override
    @Transactional
    public Post updatePost(UUID id, Post postToUpdate) {
        Post existingPost = getExistingPost(id);

        updatePostFields(existingPost, postToUpdate);
        updateCategory(existingPost, postToUpdate);
        updateTags(existingPost, postToUpdate);

        return postRepository.save(existingPost);
    }

    @Override
    @Transactional
    public void deletePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MESSAGE + id));
        postRepository.delete(post);
    }

    // =====================
    // Private Query Helpers
    // =====================

    private List<Post> findPostsByCategoryAndTag(UUID categoryId, UUID tagId) {
        Category category = categoryService.getCategoryById(categoryId);
        Tag tag = tagService.getTagById(tagId);
        return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                PostStatus.PUBLISHED, category, tag);
    }

    private List<Post> findPostsByCategory(UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, category);
    }

    private List<Post> findPostsByTag(UUID tagId) {
        Tag tag = tagService.getTagById(tagId);
        return postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED, tag);
    }

    private List<Post> findAllPublishedPosts() {
        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    // =====================
    // Private Validation & Utilities
    // =====================

    private void validatePost(Post post) {
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
    }

    private void calculateAndSetReadingTime(Post post) {
        int readingTime = calculateReadingTime(post.getContent());
        post.setReadingTime(readingTime);
    }

    private Integer calculateReadingTime(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }

    // =====================
    // Private Update Helpers
    // =====================

    private Post getExistingPost(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MESSAGE + id));
    }

    private void updatePostFields(Post existingPost, Post postToUpdate) {
        existingPost.setTitle(postToUpdate.getTitle());
        existingPost.setContent(postToUpdate.getContent());
        existingPost.setReadingTime(calculateReadingTime(postToUpdate.getContent()));
        existingPost.setStatus(postToUpdate.getStatus());
    }

    private void updateCategory(Post existingPost, Post postToUpdate) {
        if (shouldUpdateCategory(existingPost, postToUpdate)) {
            Category newCategory = categoryService.getCategoryById(postToUpdate.getCategory().getId());
            existingPost.setCategory(newCategory != null ? newCategory : postToUpdate.getCategory());
        }
    }

    private boolean shouldUpdateCategory(Post existingPost, Post postToUpdate) {
        if (postToUpdate.getCategory() == null || postToUpdate.getCategory().getId() == null) {
            return false;
        }
        if (existingPost.getCategory() == null) {
            return true;
        }
        UUID newCategoryId = postToUpdate.getCategory().getId();
        UUID existingCategoryId = existingPost.getCategory().getId();
        return !existingCategoryId.equals(newCategoryId);
    }

    private void updateTags(Post existingPost, Post postToUpdate) {
        if (hasTagsToUpdate(postToUpdate)) {
            Set<UUID> tagIdsToUpdate = extractTagIds(postToUpdate);
            Map<UUID, Tag> tagsInDb = getTagsFromDatabase(tagIdsToUpdate);

            Set<Tag> updatedTags = mergeTags(postToUpdate.getTags(), tagsInDb);
            existingPost.setTags(updatedTags);
        }
    }

    private boolean hasTagsToUpdate(Post postToUpdate) {
        return postToUpdate.getTags() != null && !postToUpdate.getTags().isEmpty();
    }

    private Set<UUID> extractTagIds(Post postToUpdate) {
        return postToUpdate.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
    }

    private Map<UUID, Tag> getTagsFromDatabase(Set<UUID> tagIds) {
        return tagService.getTagsByIds(tagIds).stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
    }

    private Set<Tag> mergeTags(Set<Tag> tagsToUpdate, Map<UUID, Tag> tagsInDb) {
        Set<Tag> mergedTags = new HashSet<>();

        for (Tag tag : tagsToUpdate) {
            mergedTags.add(tagsInDb.getOrDefault(tag.getId(), tag));
        }

        return mergedTags;
    }
}
