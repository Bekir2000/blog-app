package org.example.blogbackend.comment.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.user.model.entity.User;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // --- Calculated Fields (Read-Only) ---

    @Formula("(SELECT COUNT(*) FROM comment_likes cl WHERE cl.comment_id = id)")
    private long likesCount;

    @Formula("(SELECT COUNT(*) FROM comments c WHERE c.parent_id = id)")
    private long replyCount;

    // --- Auditing ---

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private Instant updatedAt;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @BatchSize(size = 20)
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<User> likedBy = new HashSet<>();

    // --- Lifecycle Hooks (The Fix) ---
    // These ensure dates are never null, regardless of Spring Config

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        // We generally don't set updatedAt on create if 'insertable=false' is set on the column,
        // but if your logic requires it to start as equal to createdAt, you can handle it here.
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Convenience helper
    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParent(this);
    }
}