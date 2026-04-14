package org.example.libraryms.DTO.BorrowRequest.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRItemsResponse {
    private String bookTitle;
    private String author;
    private String publisher;
    private Integer publishedYear;
    private String coverImage;
}
