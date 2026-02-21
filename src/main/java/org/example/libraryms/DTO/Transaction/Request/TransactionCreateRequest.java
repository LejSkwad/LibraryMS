package org.example.libraryms.DTO.Transaction.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequest {

    @NotNull
    private Integer borrowerId;

    @NotNull
    private Integer bookId;

    @NotNull
    private LocalDate borrowDate;

    @NotNull
    private LocalDate returnDate;
}
