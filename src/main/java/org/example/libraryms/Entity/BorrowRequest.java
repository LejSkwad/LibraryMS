package org.example.libraryms.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "borrow_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "borrowRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRequestItem> borrowRequestItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BorrowRequestStatus status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @PrePersist
    public void prePersist() {
        this.requestDate = LocalDateTime.now();
        if(this.status == null) this.status = BorrowRequestStatus.PENDING;
    }
}
