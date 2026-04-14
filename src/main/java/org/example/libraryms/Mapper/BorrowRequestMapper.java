package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.BorrowRequest.Response.BRItemsResponse;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.example.libraryms.Entity.BorrowRequest;
import org.example.libraryms.Entity.BorrowRequestItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowRequestMapper {

    @Mapping(target = "fullName", expression = "java(borrowRequest.getUser().getFirstName() + \" \" + borrowRequest.getUser().getLastName())")
    @Mapping(source = "user.memberId", target = "memberId")
    BRSearchResponse toSearchResponse(BorrowRequest borrowRequest);

    @Mapping(target = "bookTitle",    source = "book.title")
    @Mapping(target = "author",       source = "book.author")
    @Mapping(target = "publisher",    source = "book.publisher")
    @Mapping(target = "publishedYear", source = "book.publishedYear")
    @Mapping(target = "coverImage",   source = "book.coverImage")
    BRItemsResponse toItemResponse(BorrowRequestItem borrowRequestItem);
}
