package org.example.libraryms.DTO.Transaction.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequest {
    private List<Integer> bookIds;
    private Integer borrowerId;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;
}
