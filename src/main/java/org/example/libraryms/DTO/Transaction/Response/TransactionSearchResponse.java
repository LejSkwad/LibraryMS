package org.example.libraryms.DTO.Transaction.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSearchResponse {
    private Integer id;
    private String userName;
    private String socialNumber;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate returnDate;

    private String status;

}
