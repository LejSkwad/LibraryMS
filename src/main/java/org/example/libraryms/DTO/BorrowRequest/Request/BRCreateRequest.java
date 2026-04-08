package org.example.libraryms.DTO.BorrowRequest.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRCreateRequest {
    @NotEmpty
    private List<Integer> bookIds;
}
