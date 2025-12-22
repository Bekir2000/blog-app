package org.example.blogbackend.post.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class PostLike {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "user_id", nullable = false)
    private UUID userId; // We only need the ID to check uniqueness

    private PostLike(UUID userId, Post post) {
        this.userId = userId;
        this.post = post;
    }

    public static PostLike create(Post post, UUID userId) {
        return new PostLike(userId, post);
    }
}
