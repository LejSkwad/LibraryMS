package org.example.libraryms.Controller;

import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.Category.Response.CategoryResponse;
import org.example.libraryms.Service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping("/v1/category")
    public ResponseEntity<BaseResponse<List<CategoryResponse>>> getCategory() {
        List<CategoryResponse> data = categoryService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay category thanh cong"));
    }

    @PostMapping("/v1/category")
    public ResponseEntity<BaseResponse<Void>> create(@RequestParam String name) {
        categoryService.create(name);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "them category thanh cong"));
    }

    @DeleteMapping("/v1/category/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "xoa category thanh cong"));
    }
}
