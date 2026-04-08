package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Transaction.Request.TransactionCreateRequest;
import org.example.libraryms.DTO.Transaction.Request.TransactionSearchRequest;
import org.example.libraryms.DTO.Transaction.Request.TransactionUpdateRequest;
import org.example.libraryms.DTO.Transaction.Response.TransactionItemsResponse;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.example.libraryms.Entity.*;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.TransactionMapper;
import org.example.libraryms.Repository.BookRepository;
import org.example.libraryms.Repository.TransactionRepository;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Service.TransactionService;
import org.example.libraryms.Specification.TransactionSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
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
                                  BookRepository bookRepository,
                                  UserRepository userRepository) {
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
        if(transactionSearchRequest.getMemberId() != null){
            spec = spec.and(TransactionSpecification.memberIdLike(transactionSearchRequest.getMemberId()));
        }
        if(transactionSearchRequest.getBorrowDateFrom() != null || transactionSearchRequest.getBorrowDateTo() != null){
            spec = spec.and(TransactionSpecification.borrowDateBetween(transactionSearchRequest.getBorrowDateFrom(), transactionSearchRequest.getBorrowDateTo()));
        }
        if(transactionSearchRequest.getDueDateFrom() != null || transactionSearchRequest.getDueDateTo() != null){
            spec = spec.and(TransactionSpecification.dueDateBetween(transactionSearchRequest.getDueDateFrom(), transactionSearchRequest.getDueDateTo()));
        }
        if(transactionSearchRequest.getReturnDateFrom() != null || transactionSearchRequest.getReturnDateTo() != null){
            spec = spec.and(TransactionSpecification.returnDateBetween(transactionSearchRequest.getReturnDateFrom(), transactionSearchRequest.getReturnDateTo()));
        }

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
        Page<TransactionSearchResponse> responsePage = transactionPage.map(transactionMapper::toSearchResponse);

        return responsePage;
    }

    @Override
    public List<TransactionItemsResponse> getItems(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy lịch sử giao dịch"));
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
                .orElseThrow(() -> new BussinessException("Không tìm thấy tài khoản người dùng"));

        if(user.getTransactions().stream().anyMatch(
                t -> t.getStatus().equals(TransactionStatus.BORROWED))){
            throw new BussinessException("Tài khoản vẫn còn sách chưa trả");
        }

        List<Book> bookList = bookRepository.findByIdIn(transactionCreateRequest.getBookIds());
        Set<Integer> foundIds = bookList.stream().map(Book::getId).collect(Collectors.toSet());
        List<Integer> missingIds = transactionCreateRequest.getBookIds()
                .stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new BussinessException("Không thể tìm thấy sách" + missingIds);
        }

        List<String> outOfStock = bookList.stream()
                .filter(b -> b.getAvailableQuantity() <= 0)
                .map(Book::getTitle)
                .toList();

        if (!outOfStock.isEmpty()) {
            throw new BussinessException("Hết sách: " + outOfStock);
        }

        Transaction transaction = transactionMapper.fromCreate(transactionCreateRequest);
        transaction.setUser(user);
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
                .orElseThrow(() -> new BussinessException("Không tìm thấy giao dịch"));

        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.RETURNED);

        List<TransactionItem> items = transaction.getItems();
        List<Book> books = items.stream().map(TransactionItem::getBook).toList();

        books.forEach(book -> book.setAvailableQuantity(book.getAvailableQuantity() + 1));
        bookRepository.saveAll(books);
    }

    @Override
    @Transactional
    public void update(Integer id, TransactionUpdateRequest transactionUpdateRequest) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy giao dịch"));

        boolean isLibrarian = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));

        if(transaction.getStatus() == TransactionStatus.RETURNED && isLibrarian) {
            throw new BussinessException("Không thể cập nhật");
        }
        if(transaction.getStatus() == TransactionStatus.BORROWED && transactionUpdateRequest.getReturnDate() != null) {
            throw new BussinessException("Người mượn chưa trả sách");
        }
        transactionMapper.fromUpdate(transactionUpdateRequest, transaction);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy giao dịch"));
        if(transaction.getStatus() != TransactionStatus.RETURNED) {
            throw new BussinessException("Không thể xóa giao dịch chưa hoàn trả");
        }

        transactionRepository.delete(transaction);
    }
}
