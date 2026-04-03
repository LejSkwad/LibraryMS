package org.example.libraryms.DTO.Transaction.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSearchRequest {
    private String keyword;
    private String socialNumber;
    private String status;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDateFrom;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDateTo;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDateFrom;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDateTo;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate returnDateFrom;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate returnDateTo;
}
