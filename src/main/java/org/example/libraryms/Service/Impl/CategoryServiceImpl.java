package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Category.Response.CategoryResponse;
import org.example.libraryms.Entity.Category;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.CategoryMapper;
import org.example.libraryms.Repository.CategoryRepository;
import org.example.libraryms.Service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponse> findAll() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResponse> responseList = categoryList.stream().map(categoryMapper::toResponse).toList();
        return responseList;
    }

    @Override
    @Transactional
    public void create(String name) {
        if(categoryRepository.existsByNameIgnoreCaseAndTrimmed(name)){
            throw new BussinessException("category already exists");
        }

        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);

    }

    @Override
    @Transactional
    public void delete(Integer id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new BussinessException("category not found"));
        categoryRepository.deleteById(id);
    }
}
