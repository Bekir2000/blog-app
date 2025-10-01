package org.example.blogbackend.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.controller.dto.response.PostWithBookmarkResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.post.controller.dto.request.CreatePostRequest;
import org.example.blogbackend.post.controller.dto.response.PostResponse;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.service.dto.PostWithBookmark;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.common.security.BlogUserDetails;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.user.service.UserService;
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
    private final UserService userService;

    private final PostMapper postMapper;


    @GetMapping
    public ResponseEntity<List<PostWithBookmarkResponse>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        UUID userId = (blogUserDetails != null)? blogUserDetails.getUserId() : null;
        return ResponseEntity.ok(
                postService.getAllPosts(categoryId, tagId, userId)
                           .stream()
                           .map(postMapper::toPostWithBookmarkResponse)
                           .toList()
        );
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostWithBookmarkResponse> getPostById(@PathVariable UUID id, @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        UUID userId = (blogUserDetails != null)? blogUserDetails.getUserId() : null;
        PostWithBookmark post = postService.getPostById(id, userId);
        PostWithBookmarkResponse response = postMapper.toPostWithBookmarkResponse(post);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostResponse>> getDrafts(@AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        User loggedInUser = userService.getById(blogUserDetails.getUserId());
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostResponse> response = draftPosts.stream().map(postMapper::toPostResponse).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest postRequest,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        Post postToCreate = postMapper.toEntity(postRequest);

        User author = userService.getById(blogUserDetails.getUserId());
        postToCreate.setAuthor(author);

        Post createdPost = postService.createPost(postToCreate);
        PostResponse response = postMapper.toPostResponse(createdPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = "/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody CreatePostRequest updatePostRequest,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        Post postToUpdate = postMapper.toEntity(updatePostRequest);

        User author = userService.getById(blogUserDetails.getUserId());
        postToUpdate.setAuthor(author);

        Post updatedPost = postService.updatePost(postId, postToUpdate);
        PostResponse response = postMapper.toPostResponse(updatedPost);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {

        User user = userService.getById(blogUserDetails.getUserId());
        Post post = postService.toggleLike(postId, user);
        return ResponseEntity.ok(postMapper.toPostResponse(post));
    }

    @PutMapping(path = "/{postId}/bookmark")
    public ResponseEntity<PostResponse> toggleBookmark(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails blogUserDetails) {
        User user = userService.getById(blogUserDetails.getUserId());
        Post post = postService.toggleBookmark(postId, user);
        return ResponseEntity.ok(postMapper.toPostResponse(post));
    }
}
