package org.example.libraryms.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "borrow_request_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private BorrowRequest borrowRequest;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
