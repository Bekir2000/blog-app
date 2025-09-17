package org.example.blogbackend.post.model.mapper;

import org.example.blogbackend.post.model.dto.request.CreatePostRequest;
import org.example.blogbackend.post.model.dto.response.PostResponse;
import org.example.blogbackend.post.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostResponse toDto(Post post);

    Post toEntity(CreatePostRequest createPostRequest);
}
