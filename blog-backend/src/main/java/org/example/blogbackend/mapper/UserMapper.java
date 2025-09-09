package org.example.blogbackend.mapper;

import org.example.blogbackend.model.dto.requests.RegisterRequest;
import org.example.blogbackend.model.dto.responses.UserResponse;
import org.example.blogbackend.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toDto(User user);

}
