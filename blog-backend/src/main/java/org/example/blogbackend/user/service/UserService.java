package org.example.blogbackend.user.service;

import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.user.model.entity.User;

import java.util.UUID;

public interface UserService {
    User register(RegisterRequest req);
    User getById(UUID id);
    boolean existsByEmail(String email);

}
