package org.example.libraryms.DTO.Book.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchRequest {
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishedYear;
    private Integer categoryId;
    private String keyword;
}
