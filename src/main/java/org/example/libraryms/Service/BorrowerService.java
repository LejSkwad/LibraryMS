package org.example.libraryms.Service;


import org.example.libraryms.DTO.Borrower.Request.BorrowerCreateRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerSearchRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerUpdateRequest;
import org.example.libraryms.DTO.Borrower.Response.BorrowerSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowerService {
    Page<BorrowerSearchResponse> search(BorrowerSearchRequest borrowerSearchRequest, Pageable pageable);
    void create(BorrowerCreateRequest borrowerCreateRequest);
    void update(Integer id,BorrowerUpdateRequest borrowerUpdateRequest);
    void delete(Integer id);
}
