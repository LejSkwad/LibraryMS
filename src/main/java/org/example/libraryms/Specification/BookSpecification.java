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
}
