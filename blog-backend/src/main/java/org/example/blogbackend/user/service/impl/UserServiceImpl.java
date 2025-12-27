package org.example.blogbackend.user.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.mapper.PostMapper;
import org.example.blogbackend.post.model.Category;
import org.example.blogbackend.post.repository.PostRepository;
import org.example.blogbackend.shared.dto.PagedResponse;
import org.example.blogbackend.shared.mapper.PageMapper;
import org.example.blogbackend.shared.security.InputSanitizer;
import org.example.blogbackend.user.mapper.UserMapper;
import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.Bookmark;
import org.example.blogbackend.user.model.entity.Follow;
import org.example.blogbackend.user.model.entity.User;
import org.example.blogbackend.user.repository.BookmarkRepository;
import org.example.blogbackend.user.repository.FollowRepository;
import org.example.blogbackend.user.repository.UserRepository;
import org.example.blogbackend.user.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PageMapper pageMapper;

    private final PasswordEncoder passwordEncoder;

    // Security Dependencies
    private final InputSanitizer sanitizer;

    @Override
    @Transactional
    public UUID register(String firstName, String lastName, String email, String password, String profileImageUrl) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Cleaner calls
        String cleanFirstName = sanitizer.sanitizeText(firstName);
        String cleanLastName = sanitizer.sanitizeText(lastName);
        String cleanEmail = sanitizer.sanitizeText(email);
        String cleanProfileImage = sanitizer.sanitizeUrl(profileImageUrl);

        User user = User.builder()
                .email(cleanEmail)
                .firstName(cleanFirstName)
                .lastName(cleanLastName)
                .profileImageUrl(cleanProfileImage)
                .password(passwordEncoder.encode(password))
                .build();

        return userRepository.save(user).getId();
    }

    @Override
    public UserResponse getById(UUID id) {
        return userMapper.toUserResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new BadCredentialsException("User not found"))
        );
    }

    @Override
    @Transactional
    public void followUser(UUID currentUserId, UUID targetUserId) {
        List<User> users = userRepository.findAllById(List.of(currentUserId, targetUserId));

        if (users.size() != 2) {
            throw new EntityNotFoundException("One or both users not found");
        }

        User user1 = users.get(0);
        User user2 = users.get(1);

        User follower = user1.getId().equals(currentUserId) ? user1 : user2;
        User target = user2.getId().equals(targetUserId) ? user2 : user1;

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setTarget(target);

        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID currentUserId, UUID targetUserId) {
        List<User> users = userRepository.findAllById(List.of(currentUserId, targetUserId));

        if (users.size() != 2) {
            throw new EntityNotFoundException("One or both users not found");
        }

        long deletedCount = followRepository.deleteByFollowerIdAndTargetId(currentUserId, targetUserId);

        if (deletedCount == 0) {
            throw new EntityNotFoundException("Relationship not found (User was not following Target)");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserResponse> getFollowingList(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        return followRepository.findTargetsByFollowerId(userId).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void createBookmark(UUID postId, UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found with ID: " + postId);
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(userRepository.getReferenceById(userId));
        bookmark.setPost(postRepository.getReferenceById(postId));

        try {
            bookmarkRepository.save(bookmark);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("You have already bookmarked this post");
        }
    }

    @Transactional(readOnly = true)
    public PagedResponse<PostCardResponse> getBookmarksForUser(
            UUID userId,
            String title,
            Category category,
            String tag,
            String authorName,
            Pageable pageable) {

        return pageMapper.toPagedResponse(
                bookmarkRepository
                        .findBookmarksByUserId(
                                userId,
                                title,
                                category,
                                tag,
                                authorName,
                                pageable)
                        .map(postMapper::toPostCardResponse)
        );
    }

    @Override
    @Transactional
    public void deleteBookmark(UUID postId, UUID userId) {

        int rowsAffected = bookmarkRepository.deleteByPostIdAndUserId(postId, userId);

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Bookmark not found");
        }
    }
}