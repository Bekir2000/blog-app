package org.example.blogbackend.post.service;

import org.example.blogbackend.post.dto.request.PostDraftRequest;
import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.dto.response.card.draft.DraftCardResponse;
import org.example.blogbackend.post.dto.response.detail.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostDraftResult;
import org.example.blogbackend.post.dto.response.detail.draft.DraftDetailResponse;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {

    PostDraftResult createPost(UUID authorId, PostDraftRequest req);
    PagedResponse<PostCardResponse> getPostCards(
            UUID userId,
            String authorName,
            Category category,
            String title,
            String tag,
            Pageable pageable
    );
    PostDetailResponse getPostById(UUID postId, UUID userId);
    PostDraftResult saveExistingDraft(UUID postId, UUID draftId, UUID userId, PostDraftRequest req);
    PostDraftResult addNewDraft(UUID postId, UUID userId);
    DraftDetailResponse getDraftById(UUID postId, UUID draftId, UUID userId);
    PagedResponse<DraftCardResponse> getDrafts(UUID userId, Pageable pageable);
    void publish(UUID userId, UUID postId, UUID draftId);
    void deleteDraft(UUID userId, UUID postId, UUID draftId);
    void deletePost(UUID userId, UUID postId);
    void likePost(UUID postId, UUID userId);
    void unlikePost(UUID postId, UUID userId);
}
