package org.example.blogbackend.common.seeder;

import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.UserRepository;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.category.repository.CategoryRepository;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.tag.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            PostRepository postRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (postRepository.count() == 0) {
                System.out.println("🌱 Seeding database with 30 posts...");

                // 1. Create Users
                List<User> users = createUsers(passwordEncoder);
                userRepository.saveAll(users);

                // 2. Create Categories
                List<Category> categories = createCategories();
                categoryRepository.saveAll(categories);

                // 3. Create Tags
                List<Tag> tags = createTags();
                tagRepository.saveAll(tags);

                // 4. Generate 30 Posts
                List<Post> posts = new ArrayList<>();
                Random random = new Random();

                // Some placeholder images to cycle through
                String[] imageUrls = {
                        "https://miro.medium.com/v2/resize:fit:1400/format:webp/1*SdvICPEkDR4UQrThkXGKvQ.png",
                        "https://miro.medium.com/v2/resize:fit:1400/format:webp/0*KzVJGUUA2UFdSmqQ",
                        "https://miro.medium.com/v2/resize:fit:2000/format:webp/0*jNm75DMytWXufzDP",
                        "https://images.unsplash.com/photo-1498050108023-c5249f4df085",
                        "https://images.unsplash.com/photo-1555066931-4365d14bab8c"
                };

                for (int i = 1; i <= 30; i++) {
                    User author = users.get(random.nextInt(users.size()));
                    Category category = categories.get(random.nextInt(categories.size()));
                    Tag tag = tags.get(random.nextInt(tags.size()));
                    String imageUrl = imageUrls[i % imageUrls.length]; // Cycle images

                    Post post = Post.builder()
                            .title("Artificial Post #" + i + ": The Future of " + category.getName())
                            .description("This is a generated description for post number " + i + ". It contains enough text to look like a real preview on the frontend.")
                            .content("<h1>Content for Post " + i + "</h1><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>")
                            .imageUrl(imageUrl)
                            .views(random.nextInt(1000))
                            .likes(random.nextInt(100))
                            .readingTime(random.nextInt(10) + 1)
                            .status(PostStatus.PUBLISHED) // Important for Feed
                            .author(author)
                            .category(category)
                            .tags(Set.of(tag))
                            .likedBy(Set.of())
                            .createdAt(Instant.now().minusSeconds(i * 3600L)) // Stagger times so they aren't all identical
                            .updatedAt(Instant.now())
                            .build();

                    posts.add(post);
                }

                postRepository.saveAll(posts);
                System.out.println("✅ Successfully seeded 30 posts!");
            }
        };
    }

    // --- Helper Methods ---

    private List<User> createUsers(PasswordEncoder passwordEncoder) {
        return List.of(
                User.builder().username("joen").email("joe@test.com").password(passwordEncoder.encode("password")).firstName("Joe").lastName("Njenga").build(),
                User.builder().username("devrim").email("devrim@test.com").password(passwordEncoder.encode("password")).firstName("Devrim").lastName("Ozcay").build(),
                User.builder().username("abdur").email("abdur@test.com").password(passwordEncoder.encode("password")).firstName("Abdur").lastName("Rahman").build(),
                User.builder().username("admin").email("admin@test.com").password(passwordEncoder.encode("admin")).firstName("Admin").lastName("User").build()
        );
    }

    private List<Category> createCategories() {
        return List.of(
                Category.builder().name("Technology").build(),
                Category.builder().name("Lifestyle").build(),
                Category.builder().name("Coding").build(),
                Category.builder().name("AI").build()
        );
    }

    private List<Tag> createTags() {
        return List.of(
                Tag.builder().name("Java").build(),
                Tag.builder().name("Spring Boot").build(),
                Tag.builder().name("NextJS").build(),
                Tag.builder().name("Architecture").build()
        );
    }
}
