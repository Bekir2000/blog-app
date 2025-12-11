package org.example.blogbackend.user.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.post.model.entity.Post;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"posts", "comments", "likedPosts", "bookmarkedPosts", "following", "followers"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    private String profileImageUrl;

    private Instant lastActiveAt;

    // Helper to calculate status on the fly
    public boolean isOnline() {
        if (lastActiveAt == null) return false;
        // User is "Online" if active within the last 5 minutes (300 seconds)
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> likedPosts = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "likedBy")
    private Set<Comment> likedComments = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "user_bookmarks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> bookmarkedPosts = new HashSet<>();

    @Column(nullable = false)
    private Instant createdAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following = new HashSet<>();

    @Builder.Default // <--- NEW
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<User> followers = new HashSet<>();

    // ... rest of your methods (follow, unfollow, onCreate, etc.)
    public void follow(User userToFollow) {
        if (userToFollow != null) {
            this.following.add(userToFollow);
            // Ensure the other user's collection is initialized too
            // (Which @Builder.Default ensures for the adminUser as well)
            userToFollow.getFollowers().add(this);
        }
    }

    public void unfollow(User userToUnfollow) {
        if (userToUnfollow != null) {
            this.following.remove(userToUnfollow);
            userToUnfollow.getFollowers().remove(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }


    public void likePost(Post post) { likedPosts.add(post); }
    public void unlikePost(Post post) { likedPosts.remove(post); }
    public void bookmarkPost(Post post) { bookmarkedPosts.add(post); }
    public void unbookmarkPost(Post post) { bookmarkedPosts.remove(post); }
}