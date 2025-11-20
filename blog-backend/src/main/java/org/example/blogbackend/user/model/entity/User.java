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
// Exclude collections from toString/Equals/HashCode to prevent infinite loops
@ToString(exclude = {"posts", "comments", "likedPosts", "bookmarkedPosts", "following", "followers"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include // Only use ID for equals/hashcode
    private UUID id;

    // ... (Existing fields: email, password, etc. remain unchanged) ...
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

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> likedPosts = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_bookmarks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> bookmarkedPosts = new HashSet<>();

    @Column(nullable = false)
    private Instant createdAt;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_follows",
            joinColumns = @JoinColumn(name = "follower_id"), // THIS user's ID
            inverseJoinColumns = @JoinColumn(name = "followed_id") // The ID of the person being followed
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<User> followers = new HashSet<>();

    // Helper methods to keep code clean
    public void follow(User userToFollow) {
        if (userToFollow != null) {
            this.following.add(userToFollow);
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

    // (Existing helper methods for likes/bookmarks remain unchanged)
    public void likePost(Post post) { likedPosts.add(post); }
    public void unlikePost(Post post) { likedPosts.remove(post); }
    public void bookmarkPost(Post post) { bookmarkedPosts.add(post); }
    public void unbookmarkPost(Post post) { bookmarkedPosts.remove(post); }
}