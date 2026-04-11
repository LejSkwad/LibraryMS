package org.example.libraryms.DTO.Transaction.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequest {
    @NotEmpty
    private List<Integer> bookIds;

    @NotNull
    private Integer userId;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDate;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;
}
