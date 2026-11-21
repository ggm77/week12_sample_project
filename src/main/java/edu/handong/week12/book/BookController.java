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

    @Operation(summary = "도서 등록", description = "새 도서를 등록하고 201 Created와 Location 헤더를 반환합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "제목/저자 빈 값, 음수 가격, 잘못된 JSON")
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest input) {

        final BookResponse bookResponse = service.create(input);

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(bookResponse.id())
                        .toUri()
        ).body(bookResponse);
    } // TODO: @Valid, ResponseEntity 201

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest input) {
        return ResponseEntity.ok(service.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    } // TODO: 204
}
