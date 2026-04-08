package org.example.libraryms.DTO.BorrowRequest.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRSearchResponse {
    private Integer id;
    private String userName;
    private String memberId;
    private LocalDate requestDate;
    private String status;
    private List<BookInfo> books;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookInfo {
        private Integer id;
        private String title;
    }
}
