package org.example.blogbackend.post.model.projection;

import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.model.entity.Post;

import java.util.Set;

public record PostWithDetailsDto(
        Post post,
        boolean isBookmarked,
        boolean isFollowingAuthor,
        boolean isLiked
) {}
