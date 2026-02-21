package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.Category.Response.CategoryResponse;
import org.example.libraryms.Entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
}
