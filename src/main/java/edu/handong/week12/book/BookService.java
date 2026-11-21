package edu.handong.week12.book;

import edu.handong.week12.exception.CustomException;
import edu.handong.week12.exception.constants.ExceptionCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {
  private final BookRepository repository;

  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  /**
   * 페이징·정렬된 도서 목록을 조회한다.
   * - pageable에는 컨트롤러가 쿼리 파라미터(page, size, sort)로 만든 값이 들어온다.
   * - repository.findAll(pageable)이 LIMIT/OFFSET, ORDER BY가 붙은 쿼리를 실행하고,
   *   Page.map()으로 페이지 정보(totalElements 등)는 유지한 채 Book을 BookResponse로 바꾼다.
   */
  public Page<BookResponse> findAll(Pageable pageable) {
    return repository.findAll(pageable)
            .map(BookResponse::of);
  }

  public BookResponse findById(Long id) {
    return BookResponse.of(
            repository.findById(id)
                    .orElseThrow(() -> new CustomException(ExceptionCode.BOOK_NOT_FOUND))
    );
  }

  public BookResponse create(BookRequest bookRequest) {
    Book book = new Book(bookRequest.title(), bookRequest.author(), bookRequest.price());
    return BookResponse.of(repository.save(book));
  }

  public BookResponse update(Long id, BookRequest input) {
    Book book = repository.findById(id)
            .orElseThrow(() -> new CustomException(ExceptionCode.BOOK_NOT_FOUND));
    book.update(input.title(), input.author(), input.price());
    return BookResponse.of(repository.save(book));
  }

  /**
   * id에 해당하는 도서를 삭제한다.
   * - deleteById()는 없는 id여도 예외 없이 지나가므로, existsById()로 먼저 확인한다.
   * - 없으면 CustomException(BOOK_NOT_FOUND)을 던지고, 전역 예외 핸들러가 404 JSON으로 바꿔 응답한다.
   */
  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new CustomException(ExceptionCode.BOOK_NOT_FOUND);
    }
    repository.deleteById(id);
  }
}
