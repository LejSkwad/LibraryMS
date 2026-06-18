package org.example.libraryms.DTO.BorrowRequest.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRSearchResponse {
    private Integer id;
    private String fullName;
    private String memberId;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate requestDate;

    private String status;

    private String rejectionReason;
}
