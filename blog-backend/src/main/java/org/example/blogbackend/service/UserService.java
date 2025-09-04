package org.example.blogbackend.service;

import org.example.blogbackend.model.dto.requests.LoginRequest;
import org.example.blogbackend.model.dto.requests.RegisterRequest;
import org.example.blogbackend.model.entities.User;

import java.util.UUID;

public interface UserService {
    User register(RegisterRequest req);
    User getById(UUID id);
    boolean existsByEmail(String email);

}
