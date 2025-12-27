package org.example.blogbackend.post.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.model.VersionStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "post_versions")
@NoArgsConstructor
public class PostVersion {
    @Getter
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @Getter
    private Post post;

    @Column(nullable = true)
    @Getter
    private String imageUrl;

    @Getter
    @Column(columnDefinition = "TEXT")
    private String title;

    @Getter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = true)
    @Getter
    @Setter
    private Category category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "tag_name")
    @Getter
    @Setter
    private Set<String> tags = new HashSet<>();

    @Column(nullable = true, columnDefinition = "TEXT")
    @Getter
    private String description;

    @Getter
    @Enumerated(EnumType.STRING)
    private VersionStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    private PostVersion(
            UUID versionId,
            Post post,
            String title,
            String content,
            String imageUrl,
            VersionStatus status
    ) {
        this.id = versionId;
        this.post = post;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public static PostVersion createDraft(
            Post post,
            String title,
            String content,
            String imageUrl) {
        return new PostVersion(
                UUID.randomUUID(),
                post,
                title,
                content,
                imageUrl,
                VersionStatus.DRAFT);
    }

    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.updatedAt = Instant.now();
    }

    public boolean belongsToPost(UUID otherPostId) {
        if (this.post == null || otherPostId == null) {
            return false;
        }
        return this.post.getId().equals(otherPostId);
    }

    public void publish() {
        this.status = VersionStatus.PUBLISHED;
    }

    public void createDescription(String content) {
        if (content != null) {
            this.description = content.substring(0, Math.min(content.length(), 200));
        }
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean isDraft() {
        return status == VersionStatus.DRAFT;
    }

    /**
     * Self-validation logic moved here.
     * The entity ensures it meets the criteria to be published.
     */
    public void validateForPublishing() {
        if (this.title == null || this.title.trim().length() < 5) {
            throw new IllegalStateException("Cannot publish: Title must be at least 5 characters long");
        }

        if (this.content == null || this.content.trim().length() < 20) {
            throw new IllegalStateException("Cannot publish: Content is too short (min 20 chars)");
        }

        if (this.category == null) {
            throw new IllegalStateException("Cannot publish: A category must be selected");
        }

        if (this.description == null || this.description.isBlank()) {
            throw new IllegalStateException("Cannot publish: Description is required");
        }
    }
}
