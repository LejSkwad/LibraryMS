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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  TransactionItemRepository transactionItemRepository,
                                  BookRepository bookRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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

        return responseItems;
    }

    @Override
    @Transactional
    public void create(TransactionCreateRequest transactionCreateRequest) {
        User user = userRepository.findById(transactionCreateRequest.getUserId())
                .orElseThrow(() -> new BussinessException("Cannot find user"));

        List<Book> bookList = bookRepository.findByIdIn(transactionCreateRequest.getBookIds());
        Set<Integer> foundIds = bookList.stream().map(Book::getId).collect(Collectors.toSet());
        List<Integer> missingIds = transactionCreateRequest.getBookIds()
                .stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new BussinessException("Cannot find books" + missingIds);
        }

        List<String> outOfStock = bookList.stream()
                .filter(b -> b.getAvailableQuantity() <= 0)
                .map(Book::getTitle)
                .toList();

        if (!outOfStock.isEmpty()) {
            throw new BussinessException("Books out of stock: " + outOfStock);
        }

        Transaction transaction = transactionMapper.fromCreate(transactionCreateRequest);
        transaction.setUser(user);
        transaction.setSocialNumber(user.getSocialNumber());
        transaction.setStatus(TransactionStatus.BORROWED);

        List<TransactionItem> transactionItems = bookList.stream()
                        .map(book -> {
                            TransactionItem item = new TransactionItem();
                            item.setBook(book);
                            item.setTransaction(transaction);
                            return item;
                        }).toList();

        transaction.setItems(transactionItems);

        transactionRepository.save(transaction);

        bookList.forEach(book ->  book.setAvailableQuantity(book.getAvailableQuantity() - 1));
        bookRepository.saveAll(bookList);
    }

    @Override
    @Transactional
    public void bookReturn(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Cannot find transaction"));

        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.RETURNED);

        List<TransactionItem> items = transaction.getItems();
        List<Book> books = items.stream().map(TransactionItem::getBook).toList();

        books.forEach(book -> book.setAvailableQuantity(book.getAvailableQuantity() + 1));
        bookRepository.saveAll(books);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void delete(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Cannot find transaction"));
        transactionRepository.delete(transaction);
    }
}
