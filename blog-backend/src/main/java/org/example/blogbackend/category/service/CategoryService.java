package org.example.blogbackend.category.service;



import org.example.blogbackend.category.model.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listCategories();
    Category createCategory(Category category);
    void deleteCategory(UUID id);
    Category getCategoryById(UUID id);
    boolean existsById(UUID id);
    Category findOrCreateCategory(Category category);

}
