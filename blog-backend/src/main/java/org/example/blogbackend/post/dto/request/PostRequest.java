package org.example.blogbackend.post.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.blogbackend.category.model.dto.request.CreateCategoryRequest;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.tag.model.dto.request.CreateTagRequest;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

public record PostRequest(
        // 1. ALWAYS REQUIRED (Even for drafts, a post needs a title/content to exist)
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
        String content,

        @NotNull(message = "Post status is required")
        PostStatus status,

        // 2. OPTIONAL (Nullable by default, enforced ONLY if Publishing)
        String description,

        @URL(message = "Image URL must be a valid URL")
        @Size(max = 2048, message = "Image URL must not exceed {max} characters")
        String imageUrl,

        CreateCategoryRequest category,

        @Size(max = 10, message = "You can only add up to {max} tags")
        Set<CreateTagRequest> tags
) {

        // ========================================================================
        // 3. CONDITIONAL VALIDATION LOGIC
        // These run automatically when @Valid is used in the controller.
        // ========================================================================

        @AssertTrue(message = "Description is required when publishing")
        private boolean isDescriptionValid() {
                // Valid if: It's a DRAFT -OR- Description is present
                return status == PostStatus.DRAFT || (description != null && !description.isBlank());
        }

        @AssertTrue(message = "Image URL is required when publishing")
        private boolean isImageValid() {
                return status == PostStatus.DRAFT || (imageUrl != null && !imageUrl.isBlank());
        }

        @AssertTrue(message = "Category is required when publishing")
        private boolean isCategoryValid() {
                return status == PostStatus.DRAFT || (category != null && category.name() != null);
        }

        @AssertTrue(message = "At least one tag is required when publishing")
        private boolean isTagsValid() {
                return status == PostStatus.DRAFT || (tags != null && !tags.isEmpty());
        }
}
