package org.example.blogbackend.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.category.model.mapper.CategoryMapper;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.category.model.dto.response.CategoryResponse;
import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categories = categoryService.listCategories()
                .stream().map(categoryMapper::toDto)
                .toList();

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest
    ) {
        Category categoryToCreate = categoryMapper.toEntity(createCategoryRequest);
        Category safedCategory = categoryService.createCategory(categoryToCreate);

        return new ResponseEntity<>(
                categoryMapper.toDto(safedCategory),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
