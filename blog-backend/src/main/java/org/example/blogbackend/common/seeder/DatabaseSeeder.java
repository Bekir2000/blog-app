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
import java.util.List;
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

                // Users
                List<User> users = List.of(
                    User.builder()
                        .username("joen")
                        .email("joe.njenga@example.com")
                        .password(passwordEncoder.encode("password"))
                        .firstName("Joe")
                        .lastName("Njenga")
                        .build(),
                    User.builder()
                        .username("devrim")
                        .email("devrim.ozcay@example.com")
                        .password(passwordEncoder.encode("password"))
                        .firstName("Devrim")
                        .lastName("Ozcay")
                        .build(),
                    User.builder()
                        .username("abdur")
                        .email("abdur.rahman@example.com")
                        .profileImageUrl("https://miro.medium.com/v2/resize:fill:64:64/1*L6qxuEdgGIfD_4Jbg_1U9g.jpeg")
                        .password(passwordEncoder.encode("password"))
                        .firstName("Abdur")
                        .lastName("Rahman")
                        .build(),
                        User.builder()
                                .username("admin")
                                .email("admin@gmail.com")
                                .password(passwordEncoder.encode("admin"))
                                .firstName("admin")
                                .lastName("admin")
                                .build()
                );
                List<User> savedUsers = userRepository.saveAll(users);
                User joe   = savedUsers.get(0);
                User devrim= savedUsers.get(1);
                User abdur = savedUsers.get(2);

                // Category
                Category category = Category.builder()
                    .name("Technology")
                    .build();
                categoryRepository.save(category);

                // Tag
                Tag tag = Tag.builder()
                    .name("AI")
                    .build();
                tagRepository.save(tag);

                // Posts
                List<Post> posts = List.of(
                    Post.builder()
                        .title("9 Books Every AI Engineer Should Read (To Go Fully Professional)")
                        .description("Picking an AI engineering book and reading it from start to finish is tough!")
                        .content("content")
                        .imageUrl("https://miro.medium.com/v2/resize:fit:1400/format:webp/1*SdvICPEkDR4UQrThkXGKvQ.png")
                        .views(620)
                        .likes(13)
                        .readingTime(5)
                        .status(PostStatus.PUBLISHED)
                        .author(joe)
                        .category(category)
                        .tags(Set.of(tag))
                        .likedBy(Set.of())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build(),

                    Post.builder()
                        .title("Python is Dying and Nobody Wants to Admit It")
                        .description("You won’t hear this at PyCon. You won’t read it in the official Python blog. But after 2 years of Python development and...")
                        .content("content")
                        .imageUrl("https://miro.medium.com/v2/resize:fit:1400/format:webp/0*KzVJGUUA2UFdSmqQ")
                        .views(708)
                        .likes(152)
                        .readingTime(7)
                        .status(PostStatus.PUBLISHED)
                        .author(devrim)
                        .category(category)
                        .tags(Set.of(tag))
                        .likedBy(Set.of())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build(),

                    Post.builder()
                        .title("7 Python Automation Projects You Can Build in Less Than 2 Hours Each")
                        .description("Small Builds. Big Impact.")
                        .content("content")
                        .imageUrl("https://miro.medium.com/v2/resize:fit:2000/format:webp/0*jNm75DMytWXufzDP")
                        .views(352)
                        .likes(6)
                        .readingTime(10)
                        .status(PostStatus.PUBLISHED)
                        .author(abdur)
                        .category(category)
                        .tags(Set.of(tag))
                        .likedBy(Set.of())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
                );

                postRepository.saveAll(posts);
                System.out.println("✅ Seeded mock posts");
            }
        };
    }
}
