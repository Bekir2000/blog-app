package org.example.blogbackend.category.model.mapper;

import org.example.blogbackend.category.model.dto.response.CategoryResponse;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.post.model.PostStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postsCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryResponse toDto(Category category);

    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if(null == posts) return 0;
        return posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }
}
