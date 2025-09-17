package org.example.blogbackend.post.service;

import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.user.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPostById(UUID id);
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post> getDraftPosts(User author);
    Post createPost(Post postToCreate);
    Post updatePost(UUID id, Post postToUpdate);
    void deletePost(UUID id);
}
