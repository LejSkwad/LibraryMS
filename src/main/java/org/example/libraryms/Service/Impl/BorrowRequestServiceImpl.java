package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.BorrowRequest.Request.BRCreateRequest;
import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRItemsResponse;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.example.libraryms.Entity.*;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.BorrowRequestMapper;
import org.example.libraryms.Repository.*;
import org.example.libraryms.Service.BorrowRequestService;
import org.example.libraryms.Service.SseService;
import org.example.libraryms.Specification.BorrowRequestSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BorrowRequestServiceImpl implements BorrowRequestService {
    private final BorrowRequestRepository borrowRequestRepository;
    private final UserRepository userRepository;
    private final BorrowRequestMapper borrowRequestMapper;
    private final BookRepository bookRepository;
    private final SseService sseService;
    private final TransactionRepository transactionRepository;

    public BorrowRequestServiceImpl(BorrowRequestRepository borrowRequestRepository,
                                    UserRepository userRepository,
                                    BorrowRequestMapper borrowRequestMapper,
                                    BookRepository bookRepository,
                                    SseService sseService,
                                    TransactionRepository transactionRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.userRepository = userRepository;
        this.borrowRequestMapper = borrowRequestMapper;
        this.bookRepository = bookRepository;
        this.sseService = sseService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Page<BRSearchResponse> search(BRSearchRequest brSearchRequest, Pageable pageable) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth. getName());
        boolean isBorrower = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BORROWER"));

        if(isBorrower) {
            brSearchRequest.setMemberId(user.getMemberId());
        }

        Specification<BorrowRequest> spec = (root, query, builder) -> builder.conjunction();
        if(brSearchRequest.getName() != null) {
            spec = spec.and(BorrowRequestSpecification.nameLike(brSearchRequest.getName()));
        }
        if(brSearchRequest.getMemberId() != null) {
            spec = spec.and(BorrowRequestSpecification.memberIdEqual(brSearchRequest.getMemberId()));
        }
        if(brSearchRequest.getRequestDateFrom() != null || brSearchRequest.getRequestDateTo() != null) {
            spec = spec.and(BorrowRequestSpecification.requestDateBetween(brSearchRequest.getRequestDateFrom(), brSearchRequest.getRequestDateTo()));
        }
        if(brSearchRequest.getStatus() != null) {
            spec = spec.and(BorrowRequestSpecification.statusEqual(brSearchRequest.getStatus()));
        }

        Page<BorrowRequest> brPage = borrowRequestRepository.findAll(spec, pageable);
        Page<BRSearchResponse> responsePage = brPage.map(borrowRequestMapper::toSearchResponse);

        return responsePage;
    }

    @Override
    public List<BRItemsResponse> getItems(Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
        boolean isBorrower = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BORROWER"));

        BorrowRequest borrowRequest = borrowRequestRepository.findWithItemsAndBooks(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy Yêu cầu mượn"));

        if(isBorrower && !borrowRequest.getUser().getId().equals(user.getId())){
            throw new BussinessException("Không thể tìm chi tiết Yêu cầu");
        }

        List<BorrowRequestItem> borrowRequestItem = borrowRequest.getBorrowRequestItem();
        List<BRItemsResponse> responseItems = borrowRequestItem.stream()
                .map(borrowRequestMapper::toItemResponse)
                .collect(Collectors.toList());

        return responseItems;
    }

    @Override
    @Transactional
    public void create(BRCreateRequest brCreateRequest) {
        User user = userRepository.findById(brCreateRequest.getUserId())
                .orElseThrow(() -> new BussinessException("Không tìm thấy tài khoản người dùng"));

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if(!user.getEmail().equals(auth.getName())){
            throw new BussinessException("Không thể tạo yêu cầu");
        }

        if(transactionRepository.countByUser_IdAndStatus(user.getId(), TransactionStatus.BORROWED) > 0){
            throw new BussinessException("Tài khoản vẫn còn sách chưa trả");
        }

        if(borrowRequestRepository.existsByUser_IdAndStatusIn(user.getId(), List.of(BorrowRequestStatus.PENDING, BorrowRequestStatus.APPROVED))){
            throw new BussinessException("Bạn đang có yêu cầu mượn trước đó chưa hoàn thành");
        }

        List<Book> bookList = bookRepository.findByIdIn(brCreateRequest.getBookIds());
        if(brCreateRequest.getBookIds().size() != bookList.size()){
            throw new BussinessException("Một số sách không còn tồn tại");
        }

        List<String> outOfStock = bookList.stream()
                .filter(b -> b.getAvailableQuantity() <= 0)
                .map(Book::getTitle)
                .toList();

        if (!outOfStock.isEmpty()) {
            throw new BussinessException("Một số sách đã hết số lượng");
        }

        BorrowRequest borrowRequest = new BorrowRequest();
        borrowRequest.setUser(user);

        List<BorrowRequestItem> items = bookList.stream()
                .map(book -> {
                    BorrowRequestItem item = new BorrowRequestItem();
                    item.setBook(book);
                    item.setBorrowRequest(borrowRequest);
                    return item;
                }).toList();

        borrowRequest.setBorrowRequestItem(items);

        borrowRequestRepository.save(borrowRequest);
        sseService.broadcast("new_request", Map.of("requestId", borrowRequest.getId()));
    }

    @Override
    @Transactional
    public void approve(Integer id) {
        BorrowRequest borrowRequest = borrowRequestRepository.findWithUser(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy Yêu cầu mượn"));
        if(!borrowRequest.getStatus().equals(BorrowRequestStatus.PENDING)){
            throw new BussinessException("Không thể thực hiện");
        }
        borrowRequest.setStatus(BorrowRequestStatus.APPROVED);
        borrowRequestRepository.save(borrowRequest);
        sseService.sendToUser(borrowRequest.getUser().getId(), "request_approved", Map.of("requestId", id));

    }

    @Override
    @Transactional
    public void taken(Integer id, LocalDate dueDate) {
        BorrowRequest borrowRequest = borrowRequestRepository.findWithItemsAndBooks(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy Yêu cầu mượn"));

        if(!borrowRequest.getStatus().equals(BorrowRequestStatus.APPROVED)){
            throw new BussinessException("Không thể thực hiện");
        }

        List<Book> books = borrowRequest.getBorrowRequestItem()
                .stream().map(BorrowRequestItem::getBook).toList();

        Transaction transaction = new Transaction();
        transaction.setUser(borrowRequest.getUser());
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(dueDate);

        List<TransactionItem> items = books.stream().map(book -> {
            TransactionItem item = new TransactionItem();
            item.setBook(book);
            item.setTransaction(transaction);
            return item;
        }).toList();

        transaction.setItems(items);
        transactionRepository.save(transaction);

        books.forEach(b -> b.setAvailableQuantity(b.getAvailableQuantity() - 1));
        bookRepository.saveAll(books);

        borrowRequest.setStatus(BorrowRequestStatus.TAKEN);
        borrowRequestRepository.save(borrowRequest);

        sseService.sendToUser(borrowRequest.getUser().getId(), "request_taken", Map.of("requestId", id));
    }

    @Override
    @Transactional
    public void reject(Integer id, String rejectionReason) {
        BorrowRequest borrowRequest = borrowRequestRepository.findWithUser(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy Yêu cầu"));
        if(!borrowRequest.getStatus().equals(BorrowRequestStatus.PENDING)){
            throw new BussinessException("Không thể thực hiện");
        }

        borrowRequest.setStatus(BorrowRequestStatus.REJECTED);
        borrowRequest.setRejectionReason(rejectionReason);
        borrowRequestRepository.save(borrowRequest);
        sseService.sendToUser(borrowRequest.getUser().getId(), "request_rejected", Map.of("requestId", id));
    }

    @Override
    @Transactional
    public void cancel(Integer id) {
        BorrowRequest borrowRequest = borrowRequestRepository.findWithUser(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy Yêu cầu"));
        if(!borrowRequest.getStatus().equals(BorrowRequestStatus.PENDING )){
            throw new BussinessException("Không thể thực hiện");
        }

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if(!borrowRequest.getUser().getEmail().equals(auth.getName())){
            throw new BussinessException("Không thể thực hiện");
        }

        borrowRequestRepository.delete(borrowRequest);
        sseService.broadcast("request_cancelled", Map.of("requestId", id));
    }
}
