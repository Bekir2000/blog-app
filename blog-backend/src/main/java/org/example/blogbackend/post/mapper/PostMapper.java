package org.example.blogbackend.post.mapper;

import org.example.blogbackend.post.dto.response.AuthorSummary;
import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.dto.response.card.draft.DraftCardResponse;
import org.example.blogbackend.post.dto.response.detail.PostDetailResponse;
import org.example.blogbackend.post.dto.response.card.PostMetadata;
import org.example.blogbackend.post.dto.response.detail.InteractionMetrics;
import org.example.blogbackend.post.dto.response.detail.draft.DraftDetailResponse;
import org.example.blogbackend.post.model.entity.PostVersion;
import org.example.blogbackend.post.model.projection.PostWithDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostMapper {


    @Mapping(source = "post.id", target = "postId")
    public abstract DraftDetailResponse toDraftDetailResponse(PostVersion draft);

    @Mapping(source = "post.id", target = "postId")
    public abstract DraftCardResponse toDraftCardResponse(PostVersion draft);

    // 1. Main Method MUST be public
    @Mapping(source = "post.id", target = "id")
    @Mapping(source = "post.readingTime", target = "readingTime")
    @Mapping(source = "post.createdAt", target = "createdAt")
    @Mapping(source = "post.publishedVersion.title", target = "title")
    @Mapping(source = "post.publishedVersion.content", target = "content")
    @Mapping(source = "post.publishedVersion.imageUrl", target = "imageUrl")
    @Mapping(source = "post.publishedVersion.description", target = "description")
    @Mapping(target = "category", source = "post.publishedVersion.category")
    @Mapping(target = "tags", source = "post.publishedVersion.tags")
    @Mapping(source = ".", target = "metrics")
    @Mapping(source = ".", target = "author")
    public abstract PostDetailResponse toPostDetailResponse(PostWithDetailsDto dto);

    // 2. Helper Method can be PROTECTED
    // MapStruct generates a protected implementation, accessible only by the Mapper itself
    @Mapping(source = "post.author.id", target = "id")
    @Mapping(source = "post.author.firstName", target = "firstName")
    @Mapping(source = "post.author.lastName", target = "lastName")
    @Mapping(source = "post.author.profileImageUrl", target = "imageUrl")
    @Mapping(source = "isFollowingAuthor", target = "isFollowed")
    protected abstract AuthorSummary toAuthorSummary(PostWithDetailsDto dto);

    // 3. Helper Method can be PROTECTED
    @Mapping(source = "isLiked", target = "liked")
    @Mapping(source = "isBookmarked", target = "bookmarked")
    @Mapping(source = "post.likeCount", target = "likeCount")
    @Mapping(source = "post.commentCount", target = "commentCount")
    protected abstract InteractionMetrics toInteractionMetrics(PostWithDetailsDto dto);


    @Mapping(source = "post.id", target = "id")
    @Mapping(source = "post.createdAt", target = "createdAt")
    @Mapping(source = "post.publishedVersion.title", target = "title")
    @Mapping(source = "post.publishedVersion.imageUrl", target = "imageUrl")
    @Mapping(source = "post.publishedVersion.description", target = "description")
    @Mapping(target = "category", source = "post.publishedVersion.category")
    @Mapping(target = "tags", source = "post.publishedVersion.tags")
    @Mapping(source = ".", target = "meta")
    @Mapping(source = ".", target = "author")
    public abstract PostCardResponse toPostCardResponse(PostWithDetailsDto dto);

    protected abstract PostMetadata toPostMetadata(PostWithDetailsDto dto);
}
