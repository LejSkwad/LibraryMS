package org.example.libraryms.Service.Impl;

import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.example.libraryms.Repository.*;
import org.example.libraryms.Service.BorrowRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BorrowRequestServiceImpl implements BorrowRequestService {
    private final BorrowRequestRepository borrowRequestRepository;

    public BorrowRequestServiceImpl(BorrowRequestRepository borrowRequestRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @Override
    public Page<BRSearchResponse> search(BRSearchRequest brSearchRequest, Pageable pageable) {



        return null;
    }
}
