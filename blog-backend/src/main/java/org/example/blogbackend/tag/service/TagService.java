package org.example.blogbackend.tag.service;

import org.example.blogbackend.tag.model.entity.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {

    List<Tag> getTags();
    List<Tag> createTags(Set<Tag> tags);
    void deleteTag(UUID id);
    Tag getTagById(UUID id);
    List<Tag> getTagsByIds(Set<UUID> ids);
    List<Tag> findTagsByIds(Set<UUID> ids);
    boolean existsById(UUID id);
    Set<Tag> findOrCreateTagsIn(Set<Tag> tags);
}
