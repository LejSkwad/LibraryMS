package org.example.libraryms.Service;

import org.example.libraryms.DTO.Transaction.Request.TransactionCreateRequest;
import org.example.libraryms.DTO.Transaction.Request.TransactionSearchRequest;
import org.example.libraryms.DTO.Transaction.Request.TransactionUpdateRequest;
import org.example.libraryms.DTO.Transaction.Response.TransactionItemsResponse;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    Page<TransactionSearchResponse> search(TransactionSearchRequest transactionSearchRequest, Pageable pageable);
    List<TransactionItemsResponse> getItems(Integer id);
    void create(TransactionCreateRequest transactionCreateRequest);
    void bookReturn(Integer id);
    void update(Integer id, TransactionUpdateRequest transactionUpdateRequest);
    void delete(Integer id);
}
