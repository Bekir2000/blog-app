package org.example.blogbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.mapper.PostMapper;
import org.example.blogbackend.model.dto.responses.PostResponse;
import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.service.PostService;
import org.springframework.http.ResponseEntity;

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
        List<PostResponse> postResponses =  posts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) {
        Post post = postService.getPostById(id);
        PostResponse postResponse = postMapper.toDto(post);
        return ResponseEntity.ok(postResponse);
    }


}
