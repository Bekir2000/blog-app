package org.example.blogbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.model.dto.requests.LoginRequest;
import org.example.blogbackend.model.dto.requests.RegisterRequest;
import org.example.blogbackend.model.entities.User;
import org.example.blogbackend.repository.UserRepository;
import org.example.blogbackend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = User.builder()
                .email(req.email())
                .name(req.name())
                .profileImageUrl(req.profileImageUrl())
                .password(passwordEncoder.encode(req.password()))
                .build();

        return userRepository.save(user);
    }

    @Override
    public User getById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
