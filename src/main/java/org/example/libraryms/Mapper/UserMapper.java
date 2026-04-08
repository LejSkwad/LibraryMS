package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.Auth.Request.RegisterRequest;
import org.example.libraryms.DTO.User.Request.UserCreateRequest;
import org.example.libraryms.DTO.User.Request.UserUpdateRequest;
import org.example.libraryms.DTO.User.Response.UserProfileResponse;
import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.example.libraryms.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserSearchResponse toSearchResponse(User user);

    UserProfileResponse toProfileResponse(User user);

    @Mapping(target = "memberId", ignore = true)
    User fromCreate(UserCreateRequest userCreateRequest);

    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "role", ignore = true)
    User fromRegister(RegisterRequest registerRequest);

    void fromUpdate(UserUpdateRequest userUpdateRequest, @MappingTarget User user);
}
