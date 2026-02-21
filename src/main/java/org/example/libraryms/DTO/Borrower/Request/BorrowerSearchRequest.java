package org.example.libraryms.DTO.Borrower.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowerSearchRequest {
    private String keyword;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate registrationDateFrom;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate registrationDateTo;
}
