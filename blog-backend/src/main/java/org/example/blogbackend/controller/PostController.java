package org.example.blogbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.mapper.PostMapper;
import org.example.blogbackend.model.dto.requests.CreatePostRequest;
import org.example.blogbackend.model.dto.requests.UpdatePostRequest;
import org.example.blogbackend.model.dto.responses.PostResponse;
import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.model.entities.User;
import org.example.blogbackend.service.PostService;
import org.example.blogbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;


    private final PostMapper postMapper;


    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId) {

        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostResponse> postResponses =  posts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) {
        Post post = postService.getPostById(id);
        PostResponse postResponse = postMapper.toDto(post);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostResponse>> getDrafts(@RequestAttribute UUID userId) {

        User loggedInUser = userService.getById(userId);
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostResponse> postResponses = draftPosts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(postResponses);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest createPostRequest,
            @RequestAttribute UUID userId) {

        User loggedInUser = userService.getById(userId);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostResponse createdPostResponse = postMapper.toDto(createdPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPostResponse);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequest updatePostRequest) {

        Post updatedPost = postService.updatePost(id, updatePostRequest);
        return ResponseEntity.ok(postMapper.toDto(updatedPost));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }


}
