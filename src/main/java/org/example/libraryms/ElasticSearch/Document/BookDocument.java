package org.example.libraryms.ElasticSearch.Document;

import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "books")
public class BookDocument {
    @Id
    private Integer id;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String author;

    @Field(type = FieldType.Keyword)
    private String publisher;

    @Field(type = FieldType.Integer)
    private Integer publishedYear;

    @Field(type = FieldType.Integer)
    private Integer categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Integer)
    private Integer availableQuantity;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String coverImage;

    @Field(type = FieldType.Integer)
    private Integer pageCount;
}
