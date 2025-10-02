package org.example.blogbackend.user.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.post.controller.dto.response.PostResponse;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.post.service.PostService;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.UserRepository;
import org.example.blogbackend.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final PostRepository postRepository;

    @Override
    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = User.builder()
                .email(req.email())
                .username(req.username())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .profileImageUrl(req.profileImageUrl())
                .password(passwordEncoder.encode(req.password()))
                .build();

        userRepository.save(user);
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public List<Boolean> getPostBookmarkStatuses(UUID userId, List<UUID> postIds) {
        List<UUID> bookmarked = userRepository.findBookmarkedPostIdsByUserIdAndPostIdIn(userId, postIds);

        return postIds.stream().map(bookmarked::contains).toList();
    }

    @Override
    public void createBookmark(UUID postId, UUID userId) {
        Post post = findPostByIdOrThrow(postId);
        User user = getById(userId);

        user.bookmarkPost(post);
        userRepository.save(user);
    }

    @Override
    public void deleteBookmark(UUID postId, UUID userId) {
        Post post = findPostByIdOrThrow(postId);
        User user = getById(userId);

        user.unbookmarkPost(post);
        userRepository.save(user);
    }

    private Post findPostByIdOrThrow(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + id));
    }
}
