package org.example.blogbackend.common.seeder;

import net.datafaker.Faker;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.category.repository.CategoryRepository;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.tag.repository.TagRepository;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
                Faker faker = new Faker();
                System.out.println("🌱 Seeding database with Faker data...");

                // --- 1. Create Users ---
                List<User> users = new ArrayList<>();

                // 1.1 Admin User
                User adminUser = User.builder()
                        .username("admin")
                        .email("admin@test.com")
                        .password(passwordEncoder.encode("admin"))
                        .firstName("Admin")
                        .lastName("User")
                        .profileImageUrl("https://randomuser.me/api/portraits/men/85.jpg")
                        .build();
                users.add(adminUser);

                // 1.2 Random Users
                for (int i = 0; i < 10; i++) {
                    users.add(generateRandomUser(faker, passwordEncoder));
                }
                users = userRepository.saveAll(users);

                // 1.3 Set Follows
                User savedAdmin = users.get(0);
                for (int i = 1; i < users.size(); i++) {
                    savedAdmin.follow(users.get(i));
                }
                userRepository.save(savedAdmin);

                // --- 2. Create Categories ---
                Set<String> uniqueCategoryNames = new HashSet<>();
                while (uniqueCategoryNames.size() < 5) {
                    uniqueCategoryNames.add(faker.book().genre());
                }
                List<Category> categories = uniqueCategoryNames.stream()
                        .map(name -> Category.builder().name(name).build())
                        .collect(Collectors.toList());
                categories = categoryRepository.saveAll(categories);

                // --- 3. Create Tags ---
                Set<String> uniqueTagNames = new HashSet<>();
                while (uniqueTagNames.size() < 8) {
                    uniqueTagNames.add(faker.programmingLanguage().name());
                }
                List<Tag> tags = uniqueTagNames.stream()
                        .map(name -> Tag.builder().name(name).build())
                        .collect(Collectors.toList());
                tags = tagRepository.saveAll(tags);

                // --- 4. Generate 50 Posts ---
                List<Post> posts = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    posts.add(generateRandomPost(faker, i, users, categories, tags));
                }
                postRepository.saveAll(posts);

                System.out.println("✅ Successfully seeded " + posts.size() + " posts and " + users.size() + " users!");
            }
        };
    }

    // --- HELPER METHODS ---

    private User generateRandomUser(Faker faker, PasswordEncoder passwordEncoder) {
        boolean isMale = faker.bool().bool();
        String genderSlug = isMale ? "men" : "women";
        String firstName = isMale ? faker.name().maleFirstName() : faker.name().femaleFirstName();
        String lastName = faker.name().lastName();
        String username = (firstName + lastName).toLowerCase();

        int photoId = faker.number().numberBetween(1, 100);
        String avatarUrl = String.format("https://randomuser.me/api/portraits/%s/%d.jpg", genderSlug, photoId);

        return User.builder()
                .username(username)
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode("password"))
                .firstName(firstName)
                .lastName(lastName)
                .profileImageUrl(avatarUrl)
                .build();
    }

    private Post generateRandomPost(Faker faker, int index, List<User> users, List<Category> categories, List<Tag> tags) {
        // Pick random associations
        User author = users.get(faker.number().numberBetween(0, users.size()));
        Category category = categories.get(faker.number().numberBetween(0, categories.size()));
        Tag tag = tags.get(faker.number().numberBetween(0, tags.size()));

        // Create a unique image seed so every post has a different picture
        String imageUrl = "https://picsum.photos/seed/" + (index + 1) + "/800/600";

        return Post.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence(15))
                .content("<h1>" + faker.lorem().sentence() + "</h1><p>" + faker.lorem().paragraph(10) + "</p>")
                .imageUrl(imageUrl)
                .views(faker.number().numberBetween(0, 5000))
                .likes(faker.number().numberBetween(0, 500))
                .readingTime(faker.number().numberBetween(1, 15))
                .status(PostStatus.PUBLISHED)
                .author(author)
                .category(category)
                .tags(Set.of(tag))
                .likedBy(Set.of())
                .createdAt(faker.date().past(30, TimeUnit.DAYS).toInstant())
                .updatedAt(Instant.now())
                .build();
    }
}
