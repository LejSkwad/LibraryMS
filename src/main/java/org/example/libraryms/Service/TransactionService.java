package org.example.libraryms.Service;

import org.example.libraryms.DTO.Transaction.Request.TransactionSearchRequest;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Page<TransactionSearchResponse> search(TransactionSearchRequest transactionSearchRequest, Pageable pageable);

}
