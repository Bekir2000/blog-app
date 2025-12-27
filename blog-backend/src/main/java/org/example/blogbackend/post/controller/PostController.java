package org.example.blogbackend.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.dto.request.PostDraftRequest;
import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.dto.response.card.draft.DraftCardResponse;
import org.example.blogbackend.post.dto.response.detail.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostDraftResult;
import org.example.blogbackend.post.dto.response.detail.draft.DraftDetailResponse;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.security.BlogUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostById(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        UUID userId = userDetails == null ? null : userDetails.getUserId();
        return ResponseEntity.ok(
                postService.getPostById(postId, userId)
        );
    }

    @GetMapping
    public ResponseEntity<PagedResponse<PostCardResponse>> getAllPostCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag,
            @AuthenticationPrincipal BlogUserDetails userDetails){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        UUID userId = userDetails == null ? null : userDetails.getUserId();

        return ResponseEntity.ok(
                postService.getPostCards(
                        userId,
                        authorName,
                        category,
                        title,
                        tag,
                        pageable)
        );
    }

    @GetMapping("/drafts")
    public ResponseEntity<PagedResponse<DraftCardResponse>> getMyDrafts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                postService.getDrafts(userDetails.getUserId(), pageable)
        );
    }

    @GetMapping("/{postId}/drafts/{draftId}")
    public ResponseEntity<DraftDetailResponse> getDraftById(
            @PathVariable UUID postId,
            @PathVariable UUID draftId,
            @AuthenticationPrincipal BlogUserDetails userDetails) {

        return ResponseEntity.ok(
                postService.getDraftById(postId, draftId, userDetails.getUserId())
        );
    }

    @PostMapping
    public ResponseEntity<PostDraftResult> createFirstDraft(
            @Valid @RequestBody PostDraftRequest draftRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                postService.createPost(
                        userDetails.getUserId(),
                        draftRequest
                )
        );
    }

    @PostMapping("/{postId}/drafts")
    public ResponseEntity<PostDraftResult> addNewDraft(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                postService.addNewDraft(
                        postId,
                        userDetails.getUserId()
                )
        );
    }

    @PutMapping("/{postId}/drafts/{draftId}")
    public ResponseEntity<PostDraftResult> safeDraft(
            @PathVariable UUID postId,
            @PathVariable UUID draftId,
            @Valid @RequestBody PostDraftRequest draftRequest,
            @AuthenticationPrincipal BlogUserDetails userDetails){

        return ResponseEntity.ok(
                postService.saveExistingDraft(
                        postId,
                        draftId,
                        userDetails.getUserId(),
                        draftRequest
                )
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal BlogUserDetails userDetails
    ){
        postService.deletePost(userDetails.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{postId}/drafts/{draftId}")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable UUID postId,
            @PathVariable UUID draftId,
            @AuthenticationPrincipal BlogUserDetails userDetails
    ){
        postService.deleteDraft(userDetails.getUserId(), postId, draftId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{postId}/drafts/{draftId}/publish")
    public ResponseEntity<UUID> publishPost(
            @PathVariable UUID postId,
            @PathVariable UUID draftId,
            @AuthenticationPrincipal BlogUserDetails userDetails
    ){
        postService.publish(userDetails.getUserId(), postId, draftId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable UUID postId, @AuthenticationPrincipal BlogUserDetails userDetails){
        postService.likePost(postId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable UUID postId, @AuthenticationPrincipal BlogUserDetails userDetails){
        postService.unlikePost(postId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}