package org.example.libraryms.Service.Impl;

import org.example.libraryms.DTO.Transaction.Request.TransactionSearchRequest;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.example.libraryms.Entity.Transaction;
import org.example.libraryms.Entity.TransactionStatus;
import org.example.libraryms.Mapper.TransactionMapper;
import org.example.libraryms.Repository.TransactionRepository;
import org.example.libraryms.Service.TransactionService;
import org.example.libraryms.Specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository,  TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Page<TransactionSearchResponse> search(TransactionSearchRequest transactionSearchRequest, Pageable pageable) {
        Specification<Transaction> spec = null;

        if(transactionSearchRequest.getKeyword() != null){
            spec = TransactionSpecification.globalSearch(transactionSearchRequest.getKeyword());
        }
        if(transactionSearchRequest.getStatus() != null){
            spec = spec == null ? TransactionSpecification.statusEqual(transactionSearchRequest.getStatus())
                    : spec.and(TransactionSpecification.statusEqual(transactionSearchRequest.getStatus()));
        }

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        Page<TransactionSearchResponse> responsePage = transactions.map(transaction -> {
            TransactionSearchResponse response = transactionMapper.toSearchResponse(transaction);
            if(transaction.getStatus() == TransactionStatus.RETURNED){
                response.setStatus("RETURNED");
            } else if(transaction.getDueDate().isBefore(LocalDate.now())){
                response.setStatus("OVERDUE");
            } else {
                response.setStatus("BORROWED");
            }
            return response;
        });

        return responsePage;
    }
}
