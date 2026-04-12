package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.BorrowRequest.Request.BRCreateRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.example.libraryms.Entity.BorrowRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowRequestMapper {

    @Mapping(target = "fullName", expression = "java(borrowRequest.getUser().getFirstName() + \" \" + borrowRequest.getUser().getLastName())")
    @Mapping(source = "user.memberId", target = "memberId")
    BRSearchResponse toSearchResponse(BorrowRequest borrowRequest);


}
