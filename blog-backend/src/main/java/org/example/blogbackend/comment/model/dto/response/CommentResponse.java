package org.example.blogbackend.comment.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.blogbackend.post.dto.response.AuthorSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CommentResponse {
    public UUID id;
    public String content;
    public AuthorSummary author;
    public UUID postId;
    public CommentMetaData meta;
    public List<CommentResponse> replies = new ArrayList<>();
}
