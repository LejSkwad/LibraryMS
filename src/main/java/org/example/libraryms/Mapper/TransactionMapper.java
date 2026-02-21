package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.example.libraryms.Entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(target = "borrowerName", expression = "java(transaction.getBorrower().getFirstName() + \" \" + transaction.getBorrower().getLastName())")
    @Mapping(source = "borrower.socialNumber", target = "socialNumber")
    TransactionSearchResponse toSearchResponse(Transaction transaction);
}
