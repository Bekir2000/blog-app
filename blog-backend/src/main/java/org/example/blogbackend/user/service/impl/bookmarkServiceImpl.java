package org.example.blogbackend.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.dto.response.PagedResponse;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.post.model.projection.PostCardView;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.user.service.BookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class bookmarkServiceImpl implements BookmarkService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getBookmarksForUser(UUID userId, Pageable pageable) {

        Page<PostCardView> postsPage = postRepository.findBookmarkedPostsByUserId(userId, pageable);
        Page<PostCardResponse> responsePage = postsPage
                .map(post -> postMapper.toPostCardResponse(post, true));

        return postMapper.toPagedResponse(responsePage);
    }
}
