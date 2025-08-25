package org.example.blogbackend.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequestDTO {

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_ ]+$", message = "Category name can only contain letters, numbers, dashes, and spaces")
    private String name;
}
