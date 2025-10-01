package org.example.blogbackend.post.mapper;

import org.example.blogbackend.post.controller.dto.request.CreatePostRequest;
import org.example.blogbackend.post.controller.dto.response.PostResponse;
import org.example.blogbackend.post.controller.dto.response.PostWithBookmarkResponse;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.service.dto.PostWithBookmark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostWithBookmarkResponse toPostWithBookmarkResponse(PostWithBookmark post);

    PostResponse toPostResponse(Post post);

    Post toEntity(CreatePostRequest createPostRequest);
}
