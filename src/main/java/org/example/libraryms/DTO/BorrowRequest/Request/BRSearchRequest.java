package org.example.libraryms.DTO.BorrowRequest.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRSearchRequest {
    private String name;
    private String memberId;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate requestDateFrom;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate requestDateTo;

    private String status;
}
