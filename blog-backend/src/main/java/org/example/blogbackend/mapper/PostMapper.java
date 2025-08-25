package org.example.blogbackend.mapper;

import org.example.blogbackend.model.dto.requests.CreatePostRequest;
import org.example.blogbackend.model.dto.responses.PostResponse;
import org.example.blogbackend.model.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostResponse toDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "readingTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(CreatePostRequest createPostRequest);

}
