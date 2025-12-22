package org.example.blogbackend.post.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogbackend.user.model.entity.User;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    private int likeCount;

    @Column(nullable = false)
    @Getter
    private int commentCount;

    @Column(nullable = false)
    @Getter
    private int readingTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_version_id", unique = true)
    @Getter
    private PostVersion publishedVersion;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PostVersion> versions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @Getter
    private User author;

    @Column(nullable = false)
    @Getter
    private Instant createdAt;

    @Column(nullable = false)
    @Getter
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

    private Post(User author) {
        this.author = author;
    }

    public static Post create(User author) {
        return new Post(author);
    }

    public boolean isAuthor(UUID authorId) {
        return author.getId().equals(authorId);
    }

    public void calculateAndSetReadingTime(String content) {
        this.readingTime = content.split("\\s+").length / 200;
    }

    public void addVersion(PostVersion version) {
        this.versions.add(version);
    }

    public UUID publishVersion(PostVersion version) {
        version.publish();
        PostVersion oldPublishedVersion = this.publishedVersion;
        this.publishedVersion = version;

        if(oldPublishedVersion != null) return oldPublishedVersion.getId();
        return null;
    }
}