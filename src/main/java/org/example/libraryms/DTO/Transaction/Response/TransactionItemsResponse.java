package org.example.libraryms.DTO.Transaction.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionItemsResponse {
    private Integer bookId;
    private String bookTitle;
    private String author;
    private String publisher;
    private Integer publishedYear;
}
