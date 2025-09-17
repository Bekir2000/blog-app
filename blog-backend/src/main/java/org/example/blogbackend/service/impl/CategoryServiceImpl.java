package org.example.blogbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.blogbackend.model.entities.Category;
import org.example.blogbackend.repository.CategoryRepository;
import org.example.blogbackend.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional // so all db actions are in one transaction, everything or nothing
    public Category createCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())){
            throw new IllegalArgumentException("Category already exists with name: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()){
            if (!category.get().getPosts().isEmpty()){
                throw new IllegalStateException("Category has posts associated with it");
            }
            categoryRepository.deleteById(id);
        }
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id" + id));
    }

    @Override
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    @Transactional
    public Category findOrCreateCategory(Category category) {
        return categoryRepository.findByNameIgnoreCase(category.getName())
                .orElseGet(() -> {
                    // Reset ID to ensure creating a new entity
                    category.setId(null);
                    return categoryRepository.save(category);
                });
    }

}
