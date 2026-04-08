package org.example.libraryms.DTO.BorrowRequest.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRApproveRequest {
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate borrowDate;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;
}
