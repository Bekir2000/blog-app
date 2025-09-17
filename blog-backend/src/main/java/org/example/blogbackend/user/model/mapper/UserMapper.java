package org.example.blogbackend.user.model.mapper;

import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toDto(User user);

}
