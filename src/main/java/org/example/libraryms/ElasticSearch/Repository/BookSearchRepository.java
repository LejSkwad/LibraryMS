package org.example.libraryms.ElasticSearch.Repository;

import org.example.libraryms.ElasticSearch.Document.BookDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, Integer> {
}
