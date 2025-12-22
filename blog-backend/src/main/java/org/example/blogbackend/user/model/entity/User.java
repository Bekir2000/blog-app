package org.example.blogbackend.user.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogbackend.post.model.entity.Post;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String firstName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String lastName;

    private String profileImageUrl;

    private Instant lastActiveAt;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> publishedPosts = new HashSet<>();

    public User(
            String email,
            String password,
            String firstName,
            String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public static User create(
            String email,
            String password,
            String firstName,
            String lastName) {
        return new User(email, password, firstName, lastName);
    }

    public boolean isOnline() {
        if (lastActiveAt == null) return false;
        // User is "Online" if active within the last 5 minutes (300 seconds)
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    public void publishPost(Post post) {
        publishedPosts.add(post);
    }

}