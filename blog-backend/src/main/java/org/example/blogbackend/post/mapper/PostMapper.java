package org.example.blogbackend.post.mapper;

import org.example.blogbackend.post.dto.request.PostRequest;
import org.example.blogbackend.post.dto.response.PostCardResponse;
import org.example.blogbackend.post.dto.response.PostDetailResponse;
import org.example.blogbackend.post.dto.response.PostResponse;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.projection.PostCardView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostResponse toPostResponse(Post post);

    Post toEntity(PostRequest createPostRequest);

    @Mapping(target = "isBookmarked", source = "isBookmarked")
    PostCardResponse toPostCardResponse(PostCardView postCard, boolean isBookmarked);


    @Mapping(target = "isBookmarked", source = "isBookmarked")
    @Mapping(target = "followingAuthor", source = "isFollowingAuthor")
    PostDetailResponse toPostDetailResponse(Post post, Boolean isBookmarked, Boolean isFollowingAuthor);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updatePostFromRequest(PostRequest request, @MappingTarget Post post);
}
