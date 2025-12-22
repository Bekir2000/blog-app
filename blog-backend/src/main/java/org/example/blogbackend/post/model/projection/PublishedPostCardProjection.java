package org.example.blogbackend.post.model.projection;

import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.user.model.entity.User;

import org.example.blogbackend.user.model.entity.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PublishedPostCardProjection(
        UUID id,
        String title,
        String description,
        String imageUrl,

        // Flattened Author Data (No User Object)
        String authorFirstName,
        String authorLastName,
        String authorImageUrl,

        // Metrics (Directly from columns now - Super Fast!)
        int likeCount,
        int commentCount,
        int readingTime,

        // User State (Still need to check these dynamically)
        boolean isLiked,
        boolean isBookmarked,

        Instant createdAt
) {}
