package org.example.libraryms.Controller;

import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.Transaction.Request.TransactionCreateRequest;
import org.example.libraryms.DTO.Transaction.Request.TransactionSearchRequest;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.example.libraryms.Service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping("/v1/transactions")
    public ResponseEntity<BaseResponse<Page<TransactionSearchResponse>>>  getTransactions(@ModelAttribute TransactionSearchRequest transactionSearchRequest, Pageable pageable) {
        Page<TransactionSearchResponse> data = transactionService.search(transactionSearchRequest, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay transaction thanh cong"));
    }

    //@PostMapping("/v1/transactions")
    //public ResponseEntity<BaseResponse<Void>> createTransaction(@RequestBody TransactionCreateRequest transactionCreateRequest) {
        //transactionService.create(transactionCreateRequest);
    //return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Lay transaction thanh cong"));
    //}

}
