package org.example.libraryms.ElasticSearch;

import org.example.libraryms.ElasticSearch.Repository.BookSearchRepository;
import org.example.libraryms.Mapper.BookMapper;
import org.example.libraryms.Repository.BookRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BookIndexInitializer implements ApplicationRunner {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSearchRepository bookSearchRepository;

    public BookIndexInitializer(BookRepository bookRepository,
                                BookMapper bookMapper,
                                BookSearchRepository bookSearchRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookSearchRepository = bookSearchRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        bookSearchRepository.deleteAll();
        bookRepository.findAll().forEach(book -> bookSearchRepository.save(bookMapper.toDocument(book)));
    }
}
