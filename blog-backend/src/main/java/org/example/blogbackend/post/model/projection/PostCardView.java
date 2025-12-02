package org.example.blogbackend.post.model.projection;

import org.example.blogbackend.post.model.PostStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface PostCardView {

    UUID getId();

    String getTitle();

    String getDescription();

    String getImageUrl();

    int getViews();

    int getLikes(); // Uses the 'likes' column

    int getCommentsCount(); // Uses the 'commentsCount' column

    int getReadingTime();

    PostStatus getStatus();

    Instant getCreatedAt();

    AuthorInfo getAuthor();

    CategoryInfo getCategory();

    Set<TagInfo> getTags();

    // --- Nested Projections (to avoid fetching full User/Category data) ---

    interface AuthorInfo {
        UUID getId();
        String getUsername();
        String getEmail();
        String getProfileImageUrl();
    }

    interface CategoryInfo {
        UUID getId();
        String getName();
    }

    interface TagInfo {
        UUID getId();
        String getName();
    }
}
