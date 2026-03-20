package org.example.libraryms.DTO.Transaction.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateRequest {
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
}
