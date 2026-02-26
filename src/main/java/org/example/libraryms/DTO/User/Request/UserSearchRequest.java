package org.example.libraryms.DTO.User.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest {
    private String keyword;
    private String role;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate createDateFrom;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate createDateTo;
}
