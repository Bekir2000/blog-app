package org.example.blogbackend.model.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTagsRequest {

    @NotEmpty(message = "Tag names cannot be empty")
    @Size(max = 10, message = "You can only add up to {max} tags")
    private Set<
            @Size(min = 3, max = 30, message = "Tag name must be between {min} and {max} characters")
                    @Pattern(regexp = "^[a-zA-Z0-9-_ ]+$", message = "Tag name can only contain letters, numbers, dashes, and spaces")
                    String> names;
}
