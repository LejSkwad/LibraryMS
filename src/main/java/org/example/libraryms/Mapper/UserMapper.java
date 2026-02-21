package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.example.libraryms.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserSearchResponse toSearchResponse(User user);
}
