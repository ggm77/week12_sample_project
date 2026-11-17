package edu.handong.week12.book;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
  private final BookRepository repository;

  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  public List<BookResponse> findAll() {
    return repository.findAll().stream()
            .map(BookResponse::of)
            .toList();
  } // TODO STEP 6: Pagination/Sorting

  public BookResponse findById(Long id) {
    return BookResponse.of(repository.findById(id).orElseThrow());
  } // TODO STEP 5: 404

  public BookResponse create(BookRequest bookRequest) {
    Book book = new Book(bookRequest.title(), bookRequest.author(), bookRequest.price());
    return BookResponse.of(repository.save(book));
  } // TODO STEP 3: DTO

  public BookResponse update(Long id, BookRequest input) {
    Book book = repository.findById(id).orElseThrow();
    book.update(input.title(), input.author(), input.price());
    return BookResponse.of(repository.save(book));
  }

  public void delete(Long id) {
    repository.deleteById(id);
  } // TODO: 404 if absent
}
