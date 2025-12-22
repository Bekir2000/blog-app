package org.example.blogbackend.comment.model.mapper;

import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentMetaData;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.entity.Comment;
import org.example.blogbackend.comment.model.projection.CommentWithDetails;
import org.example.blogbackend.post.dto.response.card.AuthorCardSummary;
import org.example.blogbackend.post.dto.response.detail.AuthorDetailSummary;
import org.example.blogbackend.post.model.projection.PostWithDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {



    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "postId", source = "comment.postId")
    @Mapping(target = "author", source = ".")
    @Mapping(target = "meta", source = ".")
    @Mapping(target = "replies", ignore = true)
    public abstract CommentResponse toCommentResponse(CommentWithDetails c);


    @Mapping(target = "firstName", source = "authorFirstName")
    @Mapping(target = "lastName", source = "authorLastName")
    protected abstract AuthorCardSummary toAuthorDetailSummary(CommentWithDetails dto);

    @Mapping(target = "replyCount", source = "comment.replyCount")
    @Mapping(target = "likeCount", source = "comment.likeCount")
    protected abstract CommentMetaData toCommentMetaData(CommentWithDetails dto);



}
