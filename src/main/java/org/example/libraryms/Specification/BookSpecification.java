package org.example.libraryms.Specification;

import org.example.libraryms.Entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> globalSearch(String keyword) {
        return ((root, query, builder) -> {
            return builder.or(
                    builder.like(builder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("author")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("publisher")), "%" + keyword.toLowerCase() + "%")
            );
        });
    }

    public static Specification<Book> categoryEqual(Integer categoryId) {
        return ((root, query, builder) -> {
            return builder.equal(root.get("category").get("id"), categoryId);
        });
    }

    public static Specification<Book> idEqual(Integer id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }

    public static Specification<Book> titleContains(String title) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> authorContains(String author) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> publisherContains(String publisher) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%");
    }

    public static Specification<Book> publishedYearEqual(Integer year) {
        return (root, query, builder) -> builder.equal(root.get("publishedYear"), year);
    }
}
