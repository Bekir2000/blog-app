package org.example.blogbackend.mapper;

import org.example.blogbackend.model.dto.responses.TagResponse;
import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.model.entities.Tag;
import org.example.blogbackend.model.enums.PostStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "postsCount", source = "posts", qualifiedByName = "calculatePostCount")
    TagResponse toDto(Tag tag);

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts){
        if(posts == null) return 0;

        return (int) posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }
}
