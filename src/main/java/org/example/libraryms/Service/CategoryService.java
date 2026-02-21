package org.example.libraryms.Service;

import org.example.libraryms.DTO.Category.Response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> findAll();
}
