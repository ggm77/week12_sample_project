package edu.handong.week12.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Book", description = "도서 CRUD API")
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Operation(
            summary = "도서 목록 조회",
            description = "page, size, sort 파라미터로 페이징·정렬된 도서 목록을 조회합니다. 예: ?page=0&size=5&sort=price,desc"
    )
    @GetMapping
    public ResponseEntity<Page<BookResponse>> list(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "도서 단건 조회", description = "id로 도서 한 권을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 id의 도서가 없음")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> get(@Parameter(description = "도서 id") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Swagger 문서에 표시할 API 요약과 설명
    @Operation(summary = "도서 등록", description = "새 도서를 등록하고 201 Created와 Location 헤더를 반환합니다.")
    // Swagger 문서에 표시할 응답 코드별 설명 (성공 201, 검증 실패 400)
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "제목/저자 빈 값, 음수 가격, 잘못된 JSON")
    // POST /api/books 요청을 이 메서드로 연결
    @PostMapping
    // @RequestBody: 요청 JSON을 BookRequest DTO로 변환
    // @Valid: 변환 직후 BookRequest의 @NotBlank, @Min 검증을 실행
    //         → 실패하면 이 메서드 본문은 실행되지 않고 MethodArgumentNotValidException이 발생해
    //           전역 예외 핸들러가 400(INVALID_REQUEST) JSON으로 응답
    // 반환 타입 ResponseEntity: 상태 코드(201)와 헤더(Location)를 직접 지정하기 위해 사용
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest input) {

        // 검증을 통과한 DTO를 Service에 넘김
        // Service가 BookRequest → Book Entity로 바꿔 DB에 저장하고, 저장된 Entity를 BookResponse DTO로 바꿔 돌려줌
        final BookResponse bookResponse = service.create(input);

        // 201 Created 응답을 만들고, 인자로 넘긴 URI를 Location 헤더에 넣음
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        // 현재 요청 URL에서 시작
                        .fromCurrentRequest()
                        // 뒤에 "/{id}" 경로를 붙임
                        .path("/{id}")
                        // {id} 자리에 방금 생성된 도서의 id를 채움
                        .buildAndExpand(bookResponse.id())
                        // 완성된 주소를 URI 객체로 변환
                        .toUri()
        // 응답 바디에는 생성된 도서 정보(BookResponse)를 JSON으로 담음
        ).body(bookResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest input) {
        return ResponseEntity.ok(service.update(id, input));
    }

    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
