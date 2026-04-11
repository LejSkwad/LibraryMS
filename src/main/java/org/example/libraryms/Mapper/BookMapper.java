package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.Book.Request.BookCreateRequest;
import org.example.libraryms.DTO.Book.Request.BookUpdateRequest;
import org.example.libraryms.DTO.Book.Response.BookSearchResponse;
import org.example.libraryms.Entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "category.id",   target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    BookSearchResponse toSearchResponse(Book book);

    @Mapping(source = "quantity", target = "availableQuantity")
    Book fromCreate(BookCreateRequest bookCreateRequest);


    @Mapping(target = "availableQuantity", ignore = true)
    void fromUpdate(BookUpdateRequest bookUpdateRequest, @MappingTarget Book book);
}
