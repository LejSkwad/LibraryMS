package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.Transaction.Response.TransactionItemsResponse;
import org.example.libraryms.DTO.Transaction.Response.TransactionSearchResponse;
import org.example.libraryms.Entity.Transaction;
import org.example.libraryms.Entity.TransactionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "userName", expression = "java(transaction.getUser().getFirstName() + \" \" + transaction.getUser().getLastName())")
    TransactionSearchResponse toSearchResponse(Transaction transaction);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "author", source = "book.author")
    @Mapping(target = "publisher", source = "book.publisher")
    @Mapping(target = "publishedYear", source = "book.publishedYear")
    TransactionItemsResponse toItemResponse(TransactionItem transactionItem);
}
