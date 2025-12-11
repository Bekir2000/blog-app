package org.example.blogbackend.comment.model.mapper;

import org.example.blogbackend.comment.model.dto.request.CreateCommentRequest;
import org.example.blogbackend.comment.model.dto.response.CommentResponse;
import org.example.blogbackend.comment.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // 1. Entity Creation Mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "likedBy", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    Comment toComment(CreateCommentRequest request);

    // 2. Simple Response Mapping
    @Mapping(target = "likedByCurrentUser", source = "isLiked")
    // Use the entity's formula values by default
    @Mapping(target = "replyCount", source = "comment.replyCount")
    @Mapping(target = "replies", ignore = true) // Simple mapping ignores tree
    CommentResponse toCommentResponse(Comment comment, boolean isLiked);

    // 3. Tree Response Mapping (The one we use in the service)
    @Mapping(target = "likedByCurrentUser", source = "isLiked")
    @Mapping(target = "replies", source = "replies")
    // We override the formula count with the actual list size for consistency
    @Mapping(target = "replyCount", expression = "java((long) replies.size())")
    CommentResponse toTreeResponse(Comment comment, boolean isLiked, List<CommentResponse> replies);
}
