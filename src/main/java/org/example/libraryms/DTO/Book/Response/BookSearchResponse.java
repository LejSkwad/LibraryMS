package org.example.libraryms.DTO.Book.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchResponse {
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishedYear;
    private String categoryName;
    private Integer quantity;
    private Integer availableQuantity;
}
