package org.example.blogbackend.user.service;

import org.example.blogbackend.post.dto.response.PagedResponse;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookmarkService {
    PagedResponse<PostCardResponse> getBookmarksForUser(UUID userId, Pageable pageable);
}
