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

  // pageable: Controller가 쿼리 파라미터(page, size, sort)로 만들어 넘긴 페이징·정렬 정보
  //           (예: ?page=0&size=5&sort=price,desc → 0번 페이지, 5개씩, price 내림차순)
  // 반환 타입 Page<BookResponse>: 도서 목록(content)과 페이지 정보(size, number, totalElements, totalPages)를 함께 담음
  public Page<BookResponse> findAll(Pageable pageable) {
    // JpaRepository가 기본 제공하는 findAll(Pageable) 호출
    // → SQL에 ORDER BY price DESC, LIMIT 5 OFFSET 0이 붙어 실행되고,
    //   전체 개수를 구하는 count 쿼리도 함께 실행되어 Page<Book>이 만들어짐
    return repository.findAll(pageable)
            // Page 안의 Book Entity를 하나씩 BookResponse DTO로 변환
            // map()은 페이지 정보(totalElements 등)는 그대로 두고 내용물만 바꾼 새 Page를 돌려줌
            .map(BookResponse::of);
  }

  // id로 도서 한 권을 조회해서 BookResponse DTO로 돌려줌
  public BookResponse findById(Long id) {
    // 조회한 Entity를 BookResponse DTO로 바꿔서 반환 (Entity를 Controller 밖으로 내보내지 않기 위함)
    return BookResponse.of(
            // JpaRepository의 findById(): SELECT ... WHERE id = ? 실행
            // 결과가 없을 수도 있어서 Book이 아니라 Optional<Book>을 반환
            repository.findById(id)
                    // Optional에 값이 있으면 Book을 꺼내고, 비어 있으면 람다가 만든 예외를 던짐
                    // CustomException(BOOK_NOT_FOUND)은 전역 예외 핸들러(@RestControllerAdvice)가 받아
                    // 404 상태 코드와 {"code":"BOOK_NOT_FOUND","message":"책이 존재하지 않습니다."} JSON으로 응답
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
