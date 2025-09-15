package org.example.blogbackend.mapper;

import org.example.blogbackend.model.dto.requests.BulkCreateTagsRequest;
import org.example.blogbackend.model.dto.requests.CreateTagRequest;
import org.example.blogbackend.model.dto.responses.TagResponse;
import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.model.entities.Tag;
import org.example.blogbackend.model.enums.PostStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "postsCount", source = "posts", qualifiedByName = "calculatePostCount")
    TagResponse toDto(Tag tag);

    Tag toEntity(CreateTagRequest createTagRequest);

    default Set<Tag> toTagEntities(BulkCreateTagsRequest request) {
        if (request == null || request.tags() == null) {
            return Set.of();
        }
        return request.tags().stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts) {
        if (posts == null) return 0;
        return (int) posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }
}
