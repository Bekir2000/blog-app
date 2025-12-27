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

    @Bean
    CommandLineRunner seedDatabase(
            UserService userService,
            PostService postService,
            UserRepository userRepository
    ) {
        return args -> {
            // Light check to prevent re-seeding on restart
            if (userRepository.count() == 0) {
                Faker faker = new Faker();
                System.out.println("🌱 Seeding database strictly via Services...");

                // --- 1. Register Users & Capture IDs ---
                List<UUID> userIds = new ArrayList<>();

                // 1.1 Admin
                UUID adminId = userService.register(
                        "Admin",
                        "User",
                        "admin@test.com",
                        "admin",
                        "https://randomuser.me/api/portraits/men/85.jpg"
                );
                userIds.add(adminId);

                // 1.2 Random Users
                for (int i = 0; i < 10; i++) {
                    boolean isMale = faker.bool().bool();
                    String gender = isMale ? "men" : "women";
                    UUID newUserId = userService.register(
                            isMale ? faker.name().maleFirstName() : faker.name().femaleFirstName(),
                            faker.name().lastName(),
                            faker.internet().emailAddress(),
                            "password",
                            "https://randomuser.me/api/portraits/" + gender + "/" + faker.number().numberBetween(1, 99) + ".jpg"
                    );
                    userIds.add(newUserId);
                }

                System.out.println("✅ Registered " + userIds.size() + " users.");

                // --- 2. Create Interactions (Follows) ---
                // Admin follows everyone
                for (UUID targetId : userIds) {
                    if (!targetId.equals(adminId)) {
                        userService.followUser(adminId, targetId);
                    }
                }

                // --- 3. Create Posts (Draft -> Publish) ---
                List<PostDraftResult> createdPosts = new ArrayList<>();
                Category[] categories = Category.values(); // Cache enum values

                for (int i = 0; i < 50; i++) {
                    // Pick random author
                    UUID authorId = userIds.get(faker.number().numberBetween(0, userIds.size()));

                    String title = faker.book().title();
                    String content = "<h1>" + faker.lorem().sentence() + "</h1><p>" + faker.lorem().paragraph(10) + "</p>";
                    String imageUrl = "https://picsum.photos/seed/" + (i + 1) + "/800/600";

                    // Pick Random Category
                    Category randomCategory = categories[faker.number().numberBetween(0, categories.length)];

                    // Generate Random Tags
                    Set<String> tags = new HashSet<>();
                    int tagCount = faker.number().numberBetween(1, 4);
                    for(int t=0; t<tagCount; t++) {
                        tags.add(faker.programmingLanguage().name());
                    }

                    // A. Create Draft (Using the updated Request Object)
                    PostDraftRequest postDraftRequest = new PostDraftRequest(
                            title,
                            content,
                            imageUrl,
                            randomCategory,
                            tags
                    );

                    // Pass the object, not the individual strings
                    PostDraftResult result = postService.createPost(authorId, postDraftRequest);
                    createdPosts.add(result);

                    // B. Publish (Service Method)
                    // We publish 90% of posts to have content for the feed, keep 10% as drafts
                    if (faker.number().numberBetween(1, 100) > 10) {
                        postService.publish(authorId, result.postId(), result.draftId());
                    }
                }

                System.out.println("✅ Created " + createdPosts.size() + " posts via service logic.");

                // --- 4. Likes & Bookmarks ---
                for (PostDraftResult post : createdPosts) {
                    // 4.1 Random Likes
                    int interactions = faker.number().numberBetween(0, 5);
                    for (int k = 0; k < interactions; k++) {
                        UUID randomUser = userIds.get(faker.number().numberBetween(0, userIds.size()));
                        try {
                            postService.likePost(post.postId(), randomUser);
                        } catch (Exception ignored) {
                            // Ignore if user already liked
                        }
                    }

                    // 4.2 Admin Bookmarks (Randomly)
                    if (faker.bool().bool()) {
                        //userService.createBookmark(post.postId(), adminId);
                    }
                }

                System.out.println("✅ Seeding Complete!");
            }
        };
    }
}
