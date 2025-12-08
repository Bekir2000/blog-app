package org.example.blogbackend.post.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.blogbackend.post.model.PostStatus;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Important for Lombok inheritance
public class PostDetailResponse extends PostCardResponse {

    // The heavy HTML content lives ONLY here
    private String content;

    // Extra user-context specific to reading the full article
    private Boolean followingAuthor;

    private Boolean isLiked;

    // Status is usually relevant when the author views their own post details
    private PostStatus status;
}
