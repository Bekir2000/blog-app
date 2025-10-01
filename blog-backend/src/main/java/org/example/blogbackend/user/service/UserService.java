package org.example.blogbackend.user.service;

import org.example.blogbackend.auth.model.dto.request.RegisterRequest;
import org.example.blogbackend.user.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void register(RegisterRequest req);
    User getById(UUID id);
    List<Boolean> getPostBookmarkStatuses(UUID userId, List<UUID> postIds);
}
