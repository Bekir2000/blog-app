package org.example.blogbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.mapper.TagMapper;
import org.example.blogbackend.model.dto.requests.BulkCreateTagsRequest;
import org.example.blogbackend.model.dto.responses.TagResponse;
import org.example.blogbackend.model.entities.Tag;
import org.example.blogbackend.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<Tag> tags = tagService.getTags();
        List<TagResponse> tagResponses = tags.stream().map(tagMapper::toDto).toList();
        return ResponseEntity.ok(tagResponses);
    }

    @PostMapping
    public ResponseEntity<List<TagResponse>> createTags(@RequestBody BulkCreateTagsRequest bulkCreateTagsRequest) {

        Set<Tag> tagsToCreate = tagMapper.toTagEntities(bulkCreateTagsRequest);
        List<Tag> savedTags = tagService.createTags(tagsToCreate);
        List<TagResponse> createdTagResponses =  savedTags.stream().map(tagMapper::toDto).toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTagResponses);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
