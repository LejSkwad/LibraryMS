package org.example.libraryms.DTO.Transaction.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSearchResponse {
    private Integer id;
    private String fullName;
    private String memberId;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate returnDate;

    private String status;
}
