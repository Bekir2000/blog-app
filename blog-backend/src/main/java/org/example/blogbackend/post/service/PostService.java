package org.example.blogbackend.post.service;

import org.example.blogbackend.comment.model.SearchType;
import org.example.blogbackend.post.dto.request.PostRequest;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.dto.response.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostResponse;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {

    PostResponse createPost(PostRequest request, UUID userId);

    PostDetailResponse getPostById(UUID postId, UUID userId);

    PagedResponse<PostCardResponse> getPostCards(
            UUID userId, String query, SearchType searchType, UUID categoryId, UUID tagId, Pageable pageable
    );

    List<PostCardResponse> getDraftPosts(UUID userId);

    PostResponse updatePost(UUID postId, PostRequest request, UUID userId);

    void deletePost(UUID postId, UUID userId);

    PostResponse toggleLike(UUID postId, UUID userId);
}
