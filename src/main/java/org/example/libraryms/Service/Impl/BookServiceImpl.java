package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Book.Request.BookCreateRequest;
import org.example.libraryms.DTO.Book.Request.BookSearchRequest;
import org.example.libraryms.DTO.Book.Request.BookUpdateRequest;
import org.example.libraryms.DTO.Book.Response.BookSearchResponse;
import org.example.libraryms.Entity.Book;
import org.example.libraryms.Entity.Category;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.BookMapper;
import org.example.libraryms.Repository.BookRepository;
import org.example.libraryms.Repository.CategoryRepository;
import org.example.libraryms.Service.BookService;
import org.example.libraryms.Specification.BookSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           CategoryRepository categoryRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public Page<BookSearchResponse> search(BookSearchRequest bookSearchRequest,  Pageable pageable) {
        if(bookSearchRequest.getCategoryId() != null){
            Category category = categoryRepository.findById(bookSearchRequest.getCategoryId())
                    .orElseThrow(() -> new BussinessException("category not found"));
        }

        Specification<Book> spec = (root, query, builder) -> builder.conjunction();
        if (bookSearchRequest.getCategoryId() != null) {
            spec = spec.and(BookSpecification.categoryEqual(bookSearchRequest.getCategoryId()));
        }
        if (bookSearchRequest.getKeyword() != null) {
            spec = spec.and(BookSpecification.globalSearch(bookSearchRequest.getKeyword()));
        }
        if (bookSearchRequest.getId() != null) {
            spec = spec.and(BookSpecification.idEqual(bookSearchRequest.getId()));
        }
        if (bookSearchRequest.getTitle() != null && !bookSearchRequest.getTitle().isBlank()) {
            spec = spec.and(BookSpecification.titleContains(bookSearchRequest.getTitle()));
        }
        if (bookSearchRequest.getAuthor() != null && !bookSearchRequest.getAuthor().isBlank()) {
            spec = spec.and(BookSpecification.authorContains(bookSearchRequest.getAuthor()));
        }
        if (bookSearchRequest.getPublisher() != null && !bookSearchRequest.getPublisher().isBlank()) {
            spec = spec.and(BookSpecification.publisherContains(bookSearchRequest.getPublisher()));
        }
        if (bookSearchRequest.getPublishedYear() != null) {
            spec = spec.and(BookSpecification.publishedYearEqual(bookSearchRequest.getPublishedYear()));
        }
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        Page<BookSearchResponse> bookSearchResponsePage = bookPage.map(bookMapper :: toSearchResponse);

        return bookSearchResponsePage;
    }

    @Override
    @Transactional
    public void create(BookCreateRequest bookCreateRequest) {
        if(bookRepository.existsByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPublishedYear(bookCreateRequest.getTitle(), bookCreateRequest.getAuthor(), bookCreateRequest.getPublishedYear())){
            throw new BussinessException("book already exists");
        }
        Category category = categoryRepository.findById(bookCreateRequest.getCategoryId())
                .orElseThrow(() -> new BussinessException("category not found"));

        Book newBook = bookMapper.fromCreate(bookCreateRequest);
        newBook.setCategory(category);

        bookRepository.save(newBook);
    }

    @Override
    @Transactional
    public void update(Integer id, BookUpdateRequest bookUpdateRequest) {
        Book existedBook = bookRepository.findById(id)
                .orElseThrow(() -> new BussinessException("book not found"));

        int borrowed_quantity = existedBook.getQuantity() - existedBook.getAvailableQuantity();
        if(bookUpdateRequest.getQuantity() < borrowed_quantity){
            throw new BussinessException("cannot reduce total quantity below borrowed quantity");
        }

        bookMapper.fromUpdate(bookUpdateRequest, existedBook);
        existedBook.setAvailableQuantity(bookUpdateRequest.getQuantity() - borrowed_quantity);

        bookRepository.save(existedBook);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Book existedBook = bookRepository.findById(id)
                .orElseThrow(() -> new BussinessException("book not found"));

        if(existedBook.getAvailableQuantity() < existedBook.getQuantity()){
            throw new BussinessException("Book is still being borrowed, cannot delete");
        }

        bookRepository.delete(existedBook);
    }
}
