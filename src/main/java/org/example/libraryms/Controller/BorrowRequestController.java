package org.example.libraryms.Controller;

import jakarta.validation.Valid;
import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.BorrowRequest.Request.BRCreateRequest;
import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRItemsResponse;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.example.libraryms.Entity.User;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Security.JwtUtil;
import org.example.libraryms.Service.BorrowRequestService;
import org.example.libraryms.Service.SseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.util.List;

@RestController
public class BorrowRequestController {
    private final JwtUtil jwtUtil;
    private final SseService sseService;
    private final UserRepository userRepository;
    private final BorrowRequestService borrowRequestService;

    public BorrowRequestController(JwtUtil jwtUtil, SseService sseService,
                                   UserRepository userRepository,
                                   BorrowRequestService borrowRequestService) {
        this.jwtUtil = jwtUtil;
        this.sseService = sseService;
        this.userRepository = userRepository;
        this.borrowRequestService = borrowRequestService;
    }

    @GetMapping("/events")
    public SseEmitter events(@RequestParam String token) {
        jwtUtil.validateToken(token);
        String email = jwtUtil.extractClaims(token).getSubject();
        User user = userRepository.findByEmail(email);
        return sseService.subcribe(user.getId());
    }

    @GetMapping("/v1/borrow-requests")
    public ResponseEntity<BaseResponse<Page<BRSearchResponse>>> search(@ModelAttribute BRSearchRequest brSearchRequest, Pageable pageable) {
        Page<BRSearchResponse> data = borrowRequestService.search(brSearchRequest, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lấy danh sách yêu cầu thành công"));
    }

    @GetMapping("/v1/borrow-requests/{id}")
    public ResponseEntity<BaseResponse<List<BRItemsResponse>>> getItems(@PathVariable Integer id){
        List<BRItemsResponse> data = borrowRequestService.getItems(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lấy chi tiết yêu cầu thành công"));
    }

    @PostMapping("/v1/borrow-requests")
    public ResponseEntity<BaseResponse<Void>> create(@Valid @RequestBody BRCreateRequest brCreateRequest) {
        borrowRequestService.create(brCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Gửi yêu cầu mượn sách thành công"));
    }

    @PutMapping("/v1/borrow-requests/{id}/approve")
    public ResponseEntity<BaseResponse<Void>> approve(@PathVariable Integer id) {
        borrowRequestService.approve(id);
        return ResponseEntity.ok(new BaseResponse<>(null, "Duyệt yêu cầu thành công"));
    }

    @PutMapping("/v1/borrow-requests/{id}/taken")
    public ResponseEntity<BaseResponse<Void>> taken(@PathVariable Integer id, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dueDate) {
        borrowRequestService.taken(id, dueDate);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null,"Giao sách thành công"));
    }

    @PutMapping("/v1/borrow-requests/{id}/reject")
    public ResponseEntity<BaseResponse<Void>> reject(@PathVariable Integer id) {
        //borrowRequestService.reject(id);
        return ResponseEntity.ok(new BaseResponse<>(null, "Từ chối yêu cầu thành công"));
    }

    @DeleteMapping("/v1/borrow-requests/{id}")
    public ResponseEntity<BaseResponse<Void>> cancel(@PathVariable Integer id) {
        //borrowRequestService.cancel(id);
        return ResponseEntity.ok(new BaseResponse<>(null, "Hủy yêu cầu thành công"));
    }
}
