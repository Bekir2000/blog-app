package org.example.blogbackend.service;

import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.model.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPostById(UUID id);
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post> getDraftPosts(User user);
    Post createPost(Post postToCreate);
    Post updatePost(UUID id, Post postToUpdate);
    void deletePost(UUID id);
}
