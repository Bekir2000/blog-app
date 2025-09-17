package org.example.blogbackend.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorResponse {

    private UUID id;
    private String name;
    private String profileImageUrl;
}
