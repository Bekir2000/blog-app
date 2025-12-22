package org.example.blogbackend.comment.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id"),
                @Index(name = "idx_comment_parent", columnList = "parent_id")
        })
public class Comment {

    @Id
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    private UUID postId;

    @Column(nullable = false)
    @Getter
    private UUID authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Getter
    private Comment parent;

    @Getter
    private int replyCount;

    @OneToMany(
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<Comment> replies = new HashSet<>();

    @Column(length = 5000)
    @Getter
    @Setter
    private String content;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Getter
    @Setter
    private long likeCount = 0;

    @Getter
    @Setter
    @Column(nullable = false)
    private boolean deleted = false;

    protected Comment() {}

    public Comment(UUID postId, UUID authorId, Comment parent, String content) {
        this.id = UUID.randomUUID();
        this.postId = postId;
        this.authorId = authorId;
        this.parent = parent;
        this.content = content;
    }

    public static Comment createRoot(UUID postId, UUID authorId, String content) {
        return new Comment(postId, authorId, null, content);
    }

    public static Comment createReply(UUID postId, UUID authorId, Comment parent, String content) {
        return new Comment(postId, authorId, parent, content);
    }

    public boolean belongsToPost(UUID postId) {
        return this.postId.equals(postId);
    }

    public boolean isAuthor(UUID userId) {
        return this.authorId.equals(userId);
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }

}