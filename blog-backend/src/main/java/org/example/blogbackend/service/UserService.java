package org.example.blogbackend.service;

import org.example.blogbackend.model.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
