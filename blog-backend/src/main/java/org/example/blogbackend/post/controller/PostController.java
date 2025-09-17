package org.example.blogbackend.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.model.mapper.PostMapper;
import org.example.blogbackend.post.model.dto.request.CreatePostRequest;
import org.example.blogbackend.post.model.dto.response.PostResponse;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final PostMapper postMapper;


    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId) {

        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostResponse> response = posts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID id) {

        Post post = postService.getPostById(id);
        PostResponse response = postMapper.toDto(post);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostResponse>> getDrafts(@AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        User loggedInUser = blogUserDetails.authenticatedUser();
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostResponse> response = draftPosts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest postRequest,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        Post postToCreate = postMapper.toEntity(postRequest);

        User author = blogUserDetails.authenticatedUser();
        postToCreate.setAuthor(author);

        Post createdPost = postService.createPost(postToCreate);
        PostResponse response = postMapper.toDto(createdPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = "/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody CreatePostRequest updatePostRequest,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        Post postToUpdate = postMapper.toEntity(updatePostRequest);

        User author = blogUserDetails.authenticatedUser();
        postToUpdate.setAuthor(author);

        Post updatedPost = postService.updatePost(postId, postToUpdate);
        PostResponse response = postMapper.toDto(updatedPost);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

}
