package org.example.libraryms.DTO.Transaction.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSearchRequest {
    private String keyword;
    private String status;
}
