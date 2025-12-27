package org.example.blogbackend.comment.model.mapper;

import org.example.blogbackend.comment.model.dto.response.CommentMetaData;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.projection.CommentWithDetails;
import org.example.blogbackend.post.dto.response.AuthorSummary;
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


    @Mapping(target = "id", source = "authorId")
    @Mapping(target = "firstName", source = "authorFirstName")
    @Mapping(target = "lastName", source = "authorLastName")
    @Mapping(target = "imageUrl", source = "authorImageUrl")
    protected abstract AuthorSummary toAuthorDetailSummary(CommentWithDetails dto);

    @Mapping(target = "replyCount", source = "comment.replyCount")
    @Mapping(target = "likeCount", source = "comment.likeCount")
    protected abstract CommentMetaData toCommentMetaData(CommentWithDetails dto);



}
