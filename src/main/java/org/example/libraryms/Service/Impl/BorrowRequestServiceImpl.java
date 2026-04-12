package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.BorrowRequest.Request.BRCreateRequest;
import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
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

import java.util.List;
import java.util.Map;

@Service
public class BorrowRequestServiceImpl implements BorrowRequestService {
    private final BorrowRequestRepository borrowRequestRepository;
    private final UserRepository userRepository;
    private final BorrowRequestMapper borrowRequestMapper;
    private final BookRepository bookRepository;
    private final SseService sseService;

    public BorrowRequestServiceImpl(BorrowRequestRepository borrowRequestRepository,
                                    UserRepository userRepository,
                                    BorrowRequestMapper borrowRequestMapper,
                                    BookRepository bookRepository,
                                    SseService sseService) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.userRepository = userRepository;
        this.borrowRequestMapper = borrowRequestMapper;
        this.bookRepository = bookRepository;
        this.sseService = sseService;
    }

    @Override
    public Page<BRSearchResponse> search(BRSearchRequest brSearchRequest, Pageable pageable) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
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
    @Transactional
    public void create(BRCreateRequest brCreateRequest) {
        User user = userRepository.findById(brCreateRequest.getUserId())
                .orElseThrow(() -> new BussinessException("Không tìm thấy tài khoản người dùng"));

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if(!user.getEmail().equals(auth.getName())){
            throw new BussinessException("Không thể tạo yêu cầu");
        }

        if(user.getTransactions().stream().anyMatch(
                t -> t.getStatus().equals(TransactionStatus.BORROWED))){
            throw new BussinessException("Tài khoản vẫn còn sách chưa trả");
        }

        if(user.getBorrowRequests().stream().anyMatch(
                br -> br.getStatus().equals(BorrowRequestStatus.PENDING)
                        ||  br.getStatus().equals(BorrowRequestStatus.APPROVED)
        )) {
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
}
