package org.example.blogbackend.post.mapper;

import org.example.blogbackend.post.dto.response.card.PostCardResponse;
import org.example.blogbackend.post.dto.response.card.draft.DraftCardResponse;
import org.example.blogbackend.post.dto.response.detail.PostDetailResponse;
import org.example.blogbackend.post.dto.response.card.AuthorCardSummary;
import org.example.blogbackend.post.dto.response.card.PostMetadata;
import org.example.blogbackend.post.dto.response.detail.AuthorDetailSummary;
import org.example.blogbackend.post.dto.response.detail.InteractionMetrics;
import org.example.blogbackend.post.dto.response.detail.draft.DraftDetailResponse;
import org.example.blogbackend.post.model.entity.PostVersion;
import org.example.blogbackend.post.model.projection.DraftCardProjection;
import org.example.blogbackend.post.model.projection.DraftDetailDto;
import org.example.blogbackend.post.model.projection.PostWithDetailsDto;
import org.example.blogbackend.post.model.projection.PublishedPostCardProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostMapper {


    public abstract DraftDetailResponse toDraftDetailResponse(PostVersion draft);
    public abstract DraftCardResponse toDraftCardResponse(DraftCardProjection draftCardProjection);

    // 1. Main Method MUST be public
    @Mapping(source = "post.id", target = "postId")
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
    @Mapping(source = "post.author.firstName", target = "firstName")
    @Mapping(source = "post.author.lastName", target = "lastName")
    @Mapping(source = "isFollowingAuthor", target = "isFollowed")
    protected abstract AuthorDetailSummary toAuthorDetailSummary(PostWithDetailsDto dto);

    // 3. Helper Method can be PROTECTED
    @Mapping(source = "isLiked", target = "liked")
    @Mapping(source = "isBookmarked", target = "bookmarked")
    protected abstract InteractionMetrics toInteractionMetrics(PostWithDetailsDto dto);


    @Mapping(source = ".", target = "meta")
    @Mapping(source = ".", target = "author")
    public abstract PostCardResponse toPostCardResponse(PublishedPostCardProjection postCardProjection);


    @Mapping(source = "authorFirstName", target = "firstName")
    @Mapping(source = "authorLastName", target = "lastName")
    protected abstract AuthorCardSummary toAuthorCardSummary(PublishedPostCardProjection postCardProjection);

    protected abstract PostMetadata toPostMetadata(PublishedPostCardProjection postCardProjection);
}
