package org.example.blogbackend.post.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.post.model.PostStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // ✅ FIXED: Changed nullable to true so Drafts can have empty descriptions
    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int views;

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private int commentsCount;

    @Column(nullable = false)
    private int readingTime;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    // Already nullable by default, but explicit is good
    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // ✅ FIXED: Changed nullable to true so Drafts can exist without a Category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "posts_liked_by",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> likedBy = new HashSet<>();

    // ✅ PARENT LINK (For Revision/Fork Workflow)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_id")
    private Post parentPost;

    public int getLikeCount() {
        return likedBy.size();
    }

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    void update(Post post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.description = post.getDescription();
        this.imageUrl = post.getImageUrl();
        this.status = post.getStatus();
        this.category = post.getCategory(); // Ensure category updates too
        this.updatedAt = Instant.now();
    }
}