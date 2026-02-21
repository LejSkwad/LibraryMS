package org.example.libraryms.DTO.Borrower.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraryms.Entity.Borrower;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowerSearchResponse {
    private Integer id;
    private String socialNumber;
    private String fullName;
    private String phone;
    private String email;
    private String address;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate registrationDate;
    private Integer borrowingQuantity;
}
