package org.example.libraryms.Specification;

import org.example.libraryms.Entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> globalSearch(String keyword) {
        return ((root, query, builder) -> {
            return builder.or(
                    builder.like(builder.lower(root.get("username")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("firstName")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("lastName")), "%" + keyword.toLowerCase() + "%")
            );
        });
    }

    public static Specification<User> roleEqual(String role) {
        return ((root, query, builder) -> {
            return builder.equal(root.get("role"), role);
        });
    }
}
