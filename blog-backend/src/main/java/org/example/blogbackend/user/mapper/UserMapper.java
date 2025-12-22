package org.example.blogbackend.user.mapper;

import org.example.blogbackend.user.model.dto.response.UserResponse;
import org.example.blogbackend.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Explicitly tell MapStruct to use the user.isOnline() method
    @Mapping(target = "isOnline", expression = "java(user.isOnline())")
    UserResponse toUserResponse(User user);

}
