package org.example.libraryms.Mapper;

import org.example.libraryms.DTO.Borrower.Request.BorrowerCreateRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerUpdateRequest;
import org.example.libraryms.DTO.Borrower.Response.BorrowerSearchResponse;
import org.example.libraryms.Entity.Borrower;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BorrowerMapper {

    @Mapping(target = "fullName", expression = "java(borrower.getFirstName() + \" \" + borrower.getLastName())")
    BorrowerSearchResponse toSearchResponse(Borrower borrower);

    Borrower fromCreate(BorrowerCreateRequest borrowerCreateRequest);

    void fromUpdate(BorrowerUpdateRequest borrowerUpdateRequest, @MappingTarget Borrower borrower);
}
