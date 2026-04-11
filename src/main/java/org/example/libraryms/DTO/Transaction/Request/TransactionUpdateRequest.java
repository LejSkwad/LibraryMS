package org.example.libraryms.DTO.Transaction.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateRequest {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate returnDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;
}
