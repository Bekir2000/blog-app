package org.example.blogbackend.mapper;

import org.example.blogbackend.model.dto.requests.CreatePostRequest;
import org.example.blogbackend.model.dto.responses.PostResponse;
import org.example.blogbackend.model.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostResponse toDto(Post post);

    Post toEntity(CreatePostRequest createPostRequest);
}
