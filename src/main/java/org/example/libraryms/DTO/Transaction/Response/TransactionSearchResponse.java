package org.example.libraryms.DTO.Transaction.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraryms.Entity.TransactionStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSearchResponse {
    private Integer id;
    private String bookTitle;
    private String borrowerName;
    private String socialNumber;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
}
