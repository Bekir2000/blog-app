package org.example.blogbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.model.entities.Tag;
import org.example.blogbackend.repository.TagRepository;
import org.example.blogbackend.service.TagService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Transactional
    @Override
    public List<Tag> createTags(Set<Tag> tagsToCreate) {
        if (tagsToCreate.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> tagNamesToCreate = extractTagNames(tagsToCreate);
        Map<String, Tag> existingTags = findExistingTagsByName(tagNamesToCreate);

        List<Tag> newTags = filterNewTags(tagsToCreate, existingTags);
        List<Tag> savedTags = saveNewTags(newTags);

        return combineTags(savedTags, existingTags.values());
    }

    @Transactional
    @Override
    public void deleteTag(UUID id) {
        Tag tag = getTagById(id);
        validateTagDeletion(tag);
        tagRepository.delete(tag);
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID " + id));
    }

    @Override
    public List<Tag> getTagsByIds(Set<UUID> ids) {
        List<Tag> foundTags = tagRepository.findAllById(ids);
        validateAllTagsFound(ids, foundTags);
        return foundTags;
    }

    @Override
    @Transactional
    public Set<Tag> findOrCreateTagsIn(Set<Tag> tags) {
        if (tags.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> normalizedNames = extractNormalizedTagNames(tags);
        Map<String, Tag> existingTags = findExistingTagsByNormalizedName(normalizedNames);
        Map<String, Tag> newTags = identifyNewTags(tags, existingTags);

        saveNewTags(newTags.values());
        existingTags.putAll(newTags);

        return new HashSet<>(existingTags.values());
    }

    @Override
    public List<Tag> findTagsByIds(Set<UUID> ids) {
        return tagRepository.findAllById(ids);
    }

    @Override
    public boolean existsById(UUID id) {
        return tagRepository.existsById(id);
    }

    // Helper methods
    private Set<String> extractTagNames(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> extractNormalizedTagNames(Set<Tag> tags) {
        return tags.stream()
                .map(tag -> tag.getName().toLowerCase())
                .collect(Collectors.toSet());
    }

    private Map<String, Tag> findExistingTagsByName(Set<String> tagNames) {
        return tagRepository.findByNameIn(tagNames).stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));
    }

    private Map<String, Tag> findExistingTagsByNormalizedName(Set<String> normalizedNames) {
        return tagRepository.findByNameInIgnoreCase(normalizedNames).stream()
                .collect(Collectors.toMap(
                        tag -> tag.getName().toLowerCase(),
                        Function.identity()
                ));
    }

    private List<Tag> filterNewTags(Set<Tag> tagsToCreate, Map<String, Tag> existingTags) {
        return tagsToCreate.stream()
                .filter(tag -> !existingTags.containsKey(tag.getName()))
                .collect(Collectors.toList());
    }

    private Map<String, Tag> identifyNewTags(Set<Tag> tags, Map<String, Tag> existingTags) {
        return tags.stream()
                .filter(tag -> !existingTags.containsKey(tag.getName().toLowerCase()))
                .collect(Collectors.toMap(
                        tag -> tag.getName().toLowerCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    private List<Tag> saveNewTags(Collection<Tag> newTags) {
        if (newTags.isEmpty()) {
            return Collections.emptyList();
        }
        return tagRepository.saveAll(newTags);
    }

    private List<Tag> combineTags(List<Tag> savedTags, Collection<Tag> existingTags) {
        List<Tag> result = new ArrayList<>(savedTags);
        result.addAll(existingTags);
        return result;
    }

    private List<Tag> mapToOriginalOrder(Set<Tag> originalTags, Map<String, Tag> allTags) {
        return originalTags.stream()
                .map(tag -> allTags.get(tag.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    private void validateTagDeletion(Tag tag) {
        if (!tag.getPosts().isEmpty()) {
            throw new IllegalStateException("Cannot delete tag with posts");
        }
    }

    private void validateAllTagsFound(Set<UUID> requestedIds, List<Tag> foundTags) {
        if (foundTags.size() != requestedIds.size()) {
            Set<UUID> foundIds = foundTags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());
            Set<UUID> missingIds = requestedIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            throw new EntityNotFoundException("Tags not found with IDs: " + missingIds);
        }
    }
}
