package org.example.libraryms.Repository;

import org.example.libraryms.Entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = {"category"})
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    boolean existsByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPublishedYear(String title, String author, Integer publishedYear);
    List<Book> findByIdIn(List<Integer> ids);
}
