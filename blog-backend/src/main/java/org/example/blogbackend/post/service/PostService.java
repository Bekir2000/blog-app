package org.example.blogbackend.post.service;

import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.service.dto.PostWithBookmark;
import org.example.blogbackend.user.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    PostWithBookmark getPostById(UUID id, UUID userId);
    Post getPostById(UUID id);
    List<PostWithBookmark> getAllPosts(UUID categoryId, UUID tagId, UUID userId);
    List<Post> getDraftPosts(User author);
    Post createPost(Post postToCreate);
    Post updatePost(UUID id, Post postToUpdate);
    void deletePost(UUID id);
    boolean existsPostById(UUID id);
    Post toggleLike(UUID postId, User user);
    Post savePost(Post post);
    Post toggleBookmark(UUID postId, User user);
}
