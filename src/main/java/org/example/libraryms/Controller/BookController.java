package org.example.libraryms.Controller;

import jakarta.validation.Valid;
import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.Book.Request.BookCreateRequest;
import org.example.libraryms.DTO.Book.Request.BookSearchRequest;
import org.example.libraryms.DTO.Book.Request.BookUpdateRequest;
import org.example.libraryms.DTO.Book.Response.BookSearchResponse;
import org.example.libraryms.Service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/v1/books")
    public ResponseEntity<BaseResponse<Page<BookSearchResponse>>> getBooks(@ModelAttribute BookSearchRequest bookSearchRequest, Pageable pageable) {
        Page<BookSearchResponse> data = bookService.search(bookSearchRequest, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay sach thanh cong"));
    }

    @PostMapping("/v1/books")
    public ResponseEntity<BaseResponse<Void>> create(@Valid @RequestBody BookCreateRequest bookCreateRequest) {
        bookService.create(bookCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Them sach thanh cong"));
    }

    @PutMapping("/v1/books/{id}")
    public ResponseEntity<BaseResponse<Void>> update(@PathVariable Integer id, @Valid @RequestBody BookUpdateRequest bookUpdateRequest){
        bookService.update(id, bookUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Cap nhat sach thanh cong"));
    }
}
