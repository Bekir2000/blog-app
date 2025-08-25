package org.example.blogbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.mapper.PostMapper;
import org.example.blogbackend.model.entities.Category;
import org.example.blogbackend.model.entities.Post;
import org.example.blogbackend.model.entities.Tag;
import org.example.blogbackend.model.enums.PostStatus;
import org.example.blogbackend.repository.PostRepository;
import org.example.blogbackend.service.CategoryService;
import org.example.blogbackend.service.PostService;
import org.example.blogbackend.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final PostMapper postMapper;

    private static final int WORDS_PER_MINUTE = 200;

    @Override
    public Post getPostById(UUID id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if (categoryId != null && tagId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                    PostStatus.PUBLISHED,
                    category,
                    tag
            );
        }
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            return postRepository.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    category
            );
        }

        if (tagId != null) {
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(
                    PostStatus.PUBLISHED,
                    tag
            );
        }

        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }


    private Integer calculateReadingTime(String content) {
        if(content == null || content.isBlank()){
            return 0;
        }
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }

}
