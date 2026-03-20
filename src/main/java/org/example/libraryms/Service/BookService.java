package org.example.libraryms.Service;

import org.example.libraryms.DTO.Book.Request.BookCreateRequest;
import org.example.libraryms.DTO.Book.Request.BookSearchRequest;
import org.example.libraryms.DTO.Book.Request.BookUpdateRequest;
import org.example.libraryms.DTO.Book.Response.BookSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookSearchResponse> search(BookSearchRequest bookSearchRequest, Pageable pageable);
    void create(BookCreateRequest bookCreateRequest);
    void update(Integer id, BookUpdateRequest bookUpdateRequest);
}
