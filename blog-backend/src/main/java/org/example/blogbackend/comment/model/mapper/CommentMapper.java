package org.example.blogbackend.comment.model.mapper;

import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // 1. Map Entity -> Response
    @Mapping(target = "replyCount", expression = "java(comment.getReplies() != null ? comment.getReplies().size() : 0)")
    @Mapping(target = "likedByCurrentUser", source = "isLiked")
    CommentResponse toCommentResponse(Comment comment, Boolean isLiked);

    // 2. Map List<Entity> -> List<Response>
    List<CommentResponse> toCommentResponseList(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "likesCount", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    Comment toComment(CreateCommentRequest request);

}
