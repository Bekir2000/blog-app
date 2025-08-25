package org.example.blogbackend.service;

import org.example.blogbackend.model.entities.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPostById(UUID id);
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
}
