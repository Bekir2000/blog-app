package org.example.blogbackend.service;

import org.example.blogbackend.model.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {

    List<Tag> getTags();
    List<Tag> createTags(Set<Tag> tags);
    void deleteTag(UUID id);
    Tag getTagById(UUID id);
    List<Tag> getTagsByIds(Set<UUID> ids);
    boolean existsById(UUID id);
}
