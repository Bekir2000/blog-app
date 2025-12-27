package org.example.blogbackend.shared.seeder;

import net.datafaker.Faker;
import org.example.blogbackend.post.dto.request.PostDraftRequest;
import org.example.blogbackend.post.dto.response.PostDraftResult;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.user.repository.UserRepository;
import org.example.blogbackend.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class DatabaseSeeder {

    private static final String[] VALID_UNSPLASH_IDS = {
            "photo-1493612276216-ee3925520721",
            "photo-1579353977828-2a4eab54c85a",
            "photo-1517694712202-14dd9538aa97",
            "photo-1488590528505-98d2b5aba04b",
            "photo-1461749280684-dccba630e2f6"
    };

    @Bean
    CommandLineRunner seedDatabase(
            UserService userService,
            PostService postService,
            UserRepository userRepository
    ) {
        return args -> {
            if (userRepository.count() == 0) {
                Faker faker = new Faker();
                System.out.println("🌱 Seeding database strictly via Services...");

                List<UUID> userIds = new ArrayList<>();

                // --- 1.1 Admin ---
                UUID adminId = userService.register(
                        "Admin",
                        "User",
                        "admin@test.com",
                        "admin",
                        "https://randomuser.me/api/portraits/men/85.jpg"
                );
                userIds.add(adminId);

                // --- 1.2 Random Users ---
                for (int i = 0; i < 10; i++) {
                    boolean isMale = faker.bool().bool();
                    String gender = isMale ? "men" : "women";
                    String firstName = isMale ? faker.name().maleFirstName() : faker.name().femaleFirstName();
                    String lastName = faker.name().lastName();

                    // FIX: Strip everything except letters/numbers before making email
                    // e.g. "O'Connor" -> "oconnor"
                    String cleanFirst = firstName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    String cleanLast = lastName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                    String email = cleanFirst + "." + cleanLast + i + "@example.com";

                    try {
                        UUID newUserId = userService.register(
                                firstName,
                                lastName,
                                email,
                                "password",
                                "https://randomuser.me/api/portraits/" + gender + "/" + faker.number().numberBetween(1, 99) + ".jpg"
                        );
                        userIds.add(newUserId);
                    } catch (Exception e) {
                        System.err.println("Skipping user due to error: " + e.getMessage());
                    }
                }

                System.out.println("✅ Registered " + userIds.size() + " users.");

                // --- 2. Follows ---
                for (UUID targetId : userIds) {
                    if (!targetId.equals(adminId)) {
                        userService.followUser(adminId, targetId);
                    }
                }

                // --- 3. Create Posts ---
                List<PostDraftResult> createdPosts = new ArrayList<>();
                Category[] categories = Category.values();

                for (int i = 0; i < 50; i++) {
                    UUID authorId = userIds.get(faker.number().numberBetween(0, userIds.size()));

                    String title = faker.book().title();
                    // Ensure minimum title length
                    if(title.length() < 5) title = title + " - " + faker.lorem().word();

                    String content = "<h1>" + faker.lorem().sentence() + "</h1><p>" + faker.lorem().paragraph(10) + "</p>";

                    String randomUnsplashId = VALID_UNSPLASH_IDS[i % VALID_UNSPLASH_IDS.length];
                    String imageUrl = "https://images.unsplash.com/" + randomUnsplashId + "?auto=format&fit=crop&w=800&q=80";

                    Category randomCategory = categories[faker.number().numberBetween(0, categories.length)];

                    Set<String> tags = new HashSet<>();
                    int tagCount = faker.number().numberBetween(1, 4);
                    for(int t=0; t<tagCount; t++) {
                        tags.add(faker.programmingLanguage().name());
                    }

                    PostDraftRequest postDraftRequest = new PostDraftRequest(
                            title,
                            content,
                            imageUrl,
                            randomCategory,
                            tags
                    );

                    PostDraftResult result = postService.createPost(authorId, postDraftRequest);
                    createdPosts.add(result);

                    if (faker.number().numberBetween(1, 100) > 10) {
                        postService.publish(authorId, result.postId(), result.draftId());
                    }
                }

                System.out.println("✅ Created " + createdPosts.size() + " posts via service logic.");

                // --- 4. Likes ---
                for (PostDraftResult post : createdPosts) {
                    int interactions = faker.number().numberBetween(0, 5);
                    for (int k = 0; k < interactions; k++) {
                        UUID randomUser = userIds.get(faker.number().numberBetween(0, userIds.size()));
                        try {
                            postService.likePost(post.postId(), randomUser);
                        } catch (Exception ignored) { }
                    }
                }

                System.out.println("✅ Seeding Complete!");
            }
        };
    }
}
