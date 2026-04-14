package org.example.libraryms.Service;

import org.example.libraryms.DTO.BorrowRequest.Request.BRCreateRequest;
import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRItemsResponse;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRequestService {
    Page<BRSearchResponse> search(BRSearchRequest brSearchRequest, Pageable pageable);
    List<BRItemsResponse> getItems(Integer id);
    void create(BRCreateRequest brCreateRequest);
    void approve(Integer id);
    void taken(Integer id, LocalDate dueDate);
    void reject(Integer id, String rejectionReason);
    void cancel(Integer id);
}
