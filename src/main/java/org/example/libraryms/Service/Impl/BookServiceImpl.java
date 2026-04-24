package org.example.libraryms.Service.Impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Book.Request.BookCreateRequest;
import org.example.libraryms.DTO.Book.Request.BookSearchRequest;
import org.example.libraryms.DTO.Book.Request.BookUpdateRequest;
import org.example.libraryms.DTO.Book.Response.BookSearchResponse;
import org.example.libraryms.ElasticSearch.Document.BookDocument;
import org.example.libraryms.ElasticSearch.Repository.BookSearchRepository;
import org.example.libraryms.Entity.Book;
import org.example.libraryms.Entity.Category;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.BookMapper;
import org.example.libraryms.Repository.BookRepository;
import org.example.libraryms.Repository.CategoryRepository;
import org.example.libraryms.Service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final ElasticsearchOperations  elasticsearchOperations;
    private final BookSearchRepository bookSearchRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           CategoryRepository categoryRepository,
                           BookMapper bookMapper,
                           ElasticsearchOperations  elasticsearchOperations, BookSearchRepository bookSearchRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
        this.elasticsearchOperations = elasticsearchOperations;
        this.bookSearchRepository = bookSearchRepository;
    }

    @Override
    public Page<BookSearchResponse> search(BookSearchRequest bookSearchRequest,  Pageable pageable) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        if (bookSearchRequest.getIsbn() != null && !bookSearchRequest.getIsbn().isBlank()) {
            bool.filter(Query.of(q -> q.term(t -> t.field("isbn").value(bookSearchRequest.getIsbn()))));
        }
        if (bookSearchRequest.getTitle() != null && !bookSearchRequest.getTitle().isBlank()) {
            bool.must(Query.of(q -> q.multiMatch(m -> m
                    .fields("title","author")
                    .query(bookSearchRequest.getTitle())
                    .fuzziness("AUTO"))));
        }
        if (bookSearchRequest.getAuthor() != null && !bookSearchRequest.getAuthor().isBlank()) {
            bool.must(Query.of(q -> q.match(m -> m
                    .field("author")
                    .query(bookSearchRequest.getAuthor())
                    .fuzziness("AUTO"))));
        }
        if (bookSearchRequest.getCategoryId() != null) {
            bool.filter(Query.of(q -> q.term(t -> t.field("categoryId").value(bookSearchRequest.getCategoryId()))));
        }
        if (bookSearchRequest.getPublisher() != null && !bookSearchRequest.getPublisher().isBlank()) {
            bool.filter(Query.of(q -> q.term(t -> t.field("publisher").value(bookSearchRequest.getPublisher()))));
        }
        if (bookSearchRequest.getPublishedYear() != null) {
            bool.filter(Query.of(q -> q.term(t -> t.field("publishedYear").value(bookSearchRequest.getPublishedYear()))));
        }
        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withPageable(pageable)
                .build();

        SearchHits<BookDocument> hits = elasticsearchOperations.search(query, BookDocument.class);
        List<BookSearchResponse> result = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(bookMapper::fromDocument)
                .toList();

        return new PageImpl<>(result, pageable, hits.getTotalHits());
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

        bookSearchRepository.save(bookMapper.toDocument(newBook));
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

        Category category = categoryRepository.findById(bookUpdateRequest.getCategoryId())
                .orElseThrow(() -> new BussinessException("category not found"));

        bookMapper.fromUpdate(bookUpdateRequest, existedBook);
        existedBook.setCategory(category);
        existedBook.setAvailableQuantity(bookUpdateRequest.getQuantity() - borrowed_quantity);
        bookRepository.save(existedBook);

        bookSearchRepository.save(bookMapper.toDocument(existedBook));
    }
}
