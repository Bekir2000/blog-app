package org.example.blogbackend.post.model.projection;

import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.entity.PostVersion;

public record DraftDetailDto(
        PostVersion draft,
        Post post
) {}
