package org.example.blogbackend.user.model.dto.response;


import java.util.List;
import java.util.UUID;

public record BookmarkResponse(List<UUID> bookmarkedPosts) {
}
