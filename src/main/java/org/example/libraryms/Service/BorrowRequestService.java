package org.example.libraryms.Service;

import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowRequestService {
    Page<BRSearchResponse> search(BRSearchRequest brSearchRequest, Pageable pageable);
}
