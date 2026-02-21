package org.example.libraryms.Controller;

import jakarta.validation.Valid;
import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.Borrower.Request.BorrowerCreateRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerSearchRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerUpdateRequest;
import org.example.libraryms.DTO.Borrower.Response.BorrowerSearchResponse;
import org.example.libraryms.Service.BorrowerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.ObjectReadContext;

@RestController
public class BorrowerController {
    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService){
        this.borrowerService = borrowerService;
    }

    @GetMapping("/v1/borrowers")
    public ResponseEntity<BaseResponse<Page<BorrowerSearchResponse>>> getBorrowers(@ModelAttribute BorrowerSearchRequest borrowerSearchRequest, Pageable pageable){
        Page<BorrowerSearchResponse> data = borrowerService.search(borrowerSearchRequest, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay users thanh cong"));
    }

    @PostMapping("/v1/borrowers")
    public ResponseEntity<BaseResponse<Void>> create(@Valid @RequestBody BorrowerCreateRequest borrowerCreateRequest){
        borrowerService.create(borrowerCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null,"Tao user thanh cong"));
    }

    @PutMapping("/v1/borrowers/{id}")
    public ResponseEntity<BaseResponse<Void>> update(@PathVariable Integer id, @Valid @RequestBody BorrowerUpdateRequest borrowerUpdateRequest){
        borrowerService.update(id, borrowerUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null,"Cap nhat user thanh cong"));
    }

    @DeleteMapping("/v1/borrowers/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Integer id){
        borrowerService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null,"Xoa user thanh cong"));
    }
}
