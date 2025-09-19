package org.example.blogbackend.comment.model.mapper;

import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CreateCommentRequest createCommentRequest);

    @Mapping(source = "post.id", target = "postId")
    CommentResponse toDto(Comment comment);
}
