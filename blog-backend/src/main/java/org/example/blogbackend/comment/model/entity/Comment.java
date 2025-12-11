package org.example.blogbackend.comment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.user.model.entity.User;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Builder.Default
    private int likesCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // --- Replies / Nesting ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @BatchSize(size = 20) // PERFORMANCE: Loads replies for up to 20 comments in 1 query
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
