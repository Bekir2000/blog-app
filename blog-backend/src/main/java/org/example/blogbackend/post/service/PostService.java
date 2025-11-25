package org.example.blogbackend.post.service;

import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.service.dto.PostWithBookmark;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {

    PostWithBookmark getPostById(UUID id, UUID userId);

    Post getPostById(UUID id);

    /**
     * Retrieves a paginated list of posts, optionally filtered by category or tag,
     * and checks if they are bookmarked by the current user.
     */
    List<PostWithBookmark> getAllPosts(UUID categoryId, UUID tagId, UUID userId, Pageable pageable);

    List<Post> getDraftPosts(User author);

    Post createPost(Post postToCreate);

    Post updatePost(UUID id, Post postToUpdate);

    void deletePost(UUID id);

    boolean existsPostById(UUID id);

    Post toggleLike(UUID postId, User user);

    Post savePost(Post post);
}
