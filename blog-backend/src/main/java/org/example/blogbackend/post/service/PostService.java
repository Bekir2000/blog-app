package org.example.blogbackend.post.service;

import org.example.blogbackend.post.dto.request.PostRequest;
import org.example.blogbackend.post.dto.response.PagedResponse;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.dto.response.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {

    PostResponse createPost(PostRequest createPostRequest, UUID userId);

    PostDetailResponse getPostById(UUID postId, UUID userId);

    PagedResponse<PostCardResponse> getPostCards(UUID userId, UUID categoryId, UUID tagId, Pageable pageable);

    List<PostCardResponse> getDraftPosts(UUID userId);

    PostResponse updatePost(UUID postId, PostRequest request, UUID currentUserId);

    void deletePost(UUID postId, UUID userId);

    PostResponse toggleLike(UUID postId, UUID userId); // Changed return to DTO and input to UUID
}
