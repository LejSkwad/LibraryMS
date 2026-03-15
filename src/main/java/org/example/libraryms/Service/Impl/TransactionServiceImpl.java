package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Transaction.Request.TransactionCreateRequest;
import org.example.libraryms.DTO.Transaction.Request.TransactionSearchRequest;
import org.example.libraryms.DTO.Transaction.Response.TransactionItemsResponse;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.example.libraryms.Entity.*;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.TransactionMapper;
import org.example.libraryms.Repository.BookRepository;
import org.example.libraryms.Repository.TransactionItemRepository;
import org.example.libraryms.Repository.TransactionRepository;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Service.TransactionService;
import org.example.libraryms.Specification.TransactionSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionItemRepository transactionItemRepository;
    private final BookRepository bookRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  TransactionItemRepository transactionItemRepository,
                                  BookRepository bookRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionItemRepository = transactionItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Page<TransactionSearchResponse> search(TransactionSearchRequest transactionSearchRequest, Pageable pageable) {
        Specification<Transaction> spec = (root, query, builder) -> builder.conjunction();
        if(transactionSearchRequest.getStatus() != null){
            spec = spec.and(TransactionSpecification.statusEqual(transactionSearchRequest.getStatus()));
        }
        if(transactionSearchRequest.getKeyword() != null){
            spec = spec.and(TransactionSpecification.globalSearch(transactionSearchRequest.getKeyword()));
        }
        if(transactionSearchRequest.getSocialNumber() != null){
            spec = spec.and(TransactionSpecification.socialNumberLike(transactionSearchRequest.getSocialNumber()));
        }

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
        Page<TransactionSearchResponse> responsePage = transactionPage.map(transactionMapper::toSearchResponse);

        return responsePage;
    }

    @Override
    public List<TransactionItemsResponse> getItems(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Cannot find transaction"));
        List<TransactionItem> transactionItems = transaction.getItems();
        List<TransactionItemsResponse> responseItems = transactionItems.stream()
                .map(transactionMapper::toItemResponse)
                .collect(Collectors.toList());

        System.out.println(responseItems);
        return responseItems;
    }

    @Override
    @Transactional
    public void create(TransactionCreateRequest transactionCreateRequest) {

    }
}
