# Week 12 Sample Project - Book REST API (학생용 Starter)

## Project Information: 프로젝트 소개, GitHub URL, Render URL

## Installation: Java/DB 환경과 실행 방법

## Source Code Exploration: 분석 질문 8개 답변, 주석 작성한 메서드 3개

### ① Source Code Analysis

#### Project Structure & JPA

**Q1. BookController, BookService, BookRepository의 역할과 요청 처리 흐름**

`BookController`는 `/api/books`로 들어오는 HTTP 요청을 받아 URL이나 메서드에 맞는 핸들러(`list()`, `get()`, `create()`, `update()`, `delete()`)로 연결하고 결과를 JSON으로 돌려주는 웹 계층입니다. `BookService`는 Entity와 DTO간 변환, 조회 실패 처리 같은 비즈니스 로직을 담당하고(`findAll()`, `findById()`, `create()` 등), `BookRepository`는 DB 접근만 담당합니다. 즉 요청은 `Client → BookController.create() → BookService.create() → BookRepository.save() → DB` 순서로 내려갔다가, 결과가 `BookResponse`로 변환되어 역순으로 응답됩니다.
- 관련 파일: `BookController.java`, `BookService.java`, `BookRepository.java`

**Q2. `JpaRepository<Book, Long>`만 선언해도 `findAll()`, `save()`를 쓸 수 있는 이유**

`JpaRepository`가 `CrudRepository`, `PagingAndSortingRepository` 등을 상속하고 있어서 `findAll()`, `findById()`, `save()`, `deleteById()` 같은 메서드가 인터페이스에 이미 정의되어 있습니다. Spring Data JPA가 애플리케이션 시작 시 이 인터페이스의 구현체를 자동으로 만들어 Bean으로 등록하기 때문에, 직접 구현 클래스를 작성하지 않아도 `BookService`에 주입받아 사용할 수 있습니다. 제네릭 `<Book, Long>`은 각각 다룰 Entity 타입과 `@Id` 필드의 타입을 알려주는 역할입니다.
- 관련 파일: `BookRepository.java`, `BookService.findAll()`, `BookService.create()`

#### DTO & HTTP Response

**Q3. Book Entity를 직접 요청·응답에 사용할 때의 문제**

Entity를 요청 바디로 직접 받으면 클라이언트가 `id`처럼 서버가 관리해야 할 필드까지 보낼 수 있어 의도치 않은 값이 저장될 수 있습니다. 응답으로 그대로 내보내면 DB 테이블 구조가 API 스펙에 그대로 노출되고, Entity에 필드를 추가·변경하는 순간 API 응답까지 함께 바뀌어 클라이언트가 깨질 수 있습니다. 또 연관관계가 생기면 지연 로딩·순환 참조로 JSON 직렬화 오류가 날 수 있고, 검증 어노테이션(`@NotBlank` 등)을 Entity에 붙이면 DB 매핑과 입력 검증 책임이 섞이게 됩니다.
- 관련 파일: `Book.java`

**Q4. Request DTO와 Response DTO를 분리하는 이유와 ResponseEntity가 필요한 상황**

`BookRequest`는 클라이언트가 보낼 수 있는 값과 검증 규칙만 갖고, `BookResponse`는 클라이언트에게 보여줄 값만 갖도록 분리하면 입력과 출력의 스펙을 각각 독립적으로 관리할 수 있습니다. 변환은 `BookResponse.of(Book)`와 `BookService.create()` 안의 `new Book(...)`처럼 서비스 계층에서 한 번만 일어나게 됩니다. `ResponseEntity`는 상태 코드나 헤더를 직접 정해야 할 때 필요한데, 예를 들어 생성 시 `201 Created` + `Location` 헤더, 삭제 시 `204 No Content`, 데이터가 없을 때 `404 Not Found`를 돌려주는 경우입니다.
- 관련 파일: `BookRequest.java`, `BookResponse.of()`, `BookController.create()`, `BookController.delete()`

#### Validation & Exception Handling

**Q5. `@Valid`, `@NotBlank`, `@Min`의 동작 시점**

`@Valid`는 `BookController.create()`/`update()`의 `@RequestBody BookRequest` 앞에 붙어 있어서, JSON이 `BookRequest`로 변환된 직후·컨트롤러 메서드가 실행되기 전에 검증을 실행하라고 지시합니다. 이때 `@NotBlank`는 `title`, `author`가 null이거나 공백만 있는지 검사하고, `@Min(0)`은 `price`가 0보다 작은지 검사합니다. 하나라도 실패하면 메서드 본문은 실행되지 않고 `MethodArgumentNotValidException`이 발생해 기본적으로 `400 Bad Request`가 응답됩니다.
- 관련 파일: `BookRequest.java`, `BookController.create()`, `BookController.update()`

**Q6. `@RestControllerAdvice`와 `@ExceptionHandler` 사용 시 예외 처리 흐름 변화**

지금은 `BookService.findById()`에서 `orElseThrow()`가 던지는 `NoSuchElementException`을 아무도 처리하지 않아서, 없는 id를 조회하면 Spring 기본 에러 응답(500)이 나갑니다. `@RestControllerAdvice` 클래스를 만들고 그 안에 `@ExceptionHandler(BookNotFoundException.class)`, `@ExceptionHandler(MethodArgumentNotValidException.class)` 같은 메서드를 두면, 컨트롤러 밖으로 던져진 예외가 이 메서드로 모여 원하는 상태 코드(404, 400)와 통일된 에러 JSON으로 변환됩니다. 결과적으로 각 컨트롤러마다 try-catch를 쓸 필요 없이 예외 처리를 한 곳에서 일관되게 관리할 수 있습니다.
- 관련 파일: `BookService.findById()`, `BookService.update()`, `BookService.delete()`, `GlobalExceptionHandler.java`

#### Pagination & OpenAPI

**Q7. `Pageable`과 `Page<T>`의 역할, `sort=price,desc` 처리 방식**

`Pageable`은 "몇 번째 페이지를, 몇 개씩, 어떤 정렬로" 가져올지를 담은 요청 정보이고, 컨트롤러 파라미터로 선언하면 Spring이 쿼리 파라미터 `page`, `size`, `sort`를 읽어 자동으로 만들어 줍니다. `Page<T>`는 그 결과로, 실제 데이터 목록(`content`)과 함께 전체 개수(`totalElements`), 전체 페이지 수(`totalPages`) 등 페이지 메타정보를 담고 있습니다. `sort=price,desc`는 `Sort.by(Sort.Direction.DESC, "price")`로 해석되어 `repository.findAll(pageable)` 실행 시 SQL에 `ORDER BY price DESC`와 `LIMIT/OFFSET`이 붙습니다.
- 관련 파일: `BookController.list(Pageable)`, `BookService.findAll(Pageable)`, `BookRepository.findAll(Pageable)`

**Q8. `/v3/api-docs`와 `/swagger-ui.html`의 차이**

`/v3/api-docs`는 springdoc-openapi가 컨트롤러의 매핑·DTO·검증 어노테이션을 분석해서 만든 OpenAPI 명세를 JSON 데이터로 제공하는 엔드포인트로, 다른 도구가 읽기 위한 기계용 문서입니다. `/swagger-ui.html`은 그 JSON을 읽어 사람이 브라우저에서 보기 좋게 렌더링한 UI 화면으로, 각 API를 펼쳐 보고 "Try it out"으로 직접 요청을 보내볼 수 있습니다. 즉 `/v3/api-docs`가 원본 데이터이고 `/swagger-ui.html`은 그 데이터를 보여주는 뷰어입니다.
- 관련 파일: `build.gradle` (`springdoc-openapi-starter-webmvc-ui` 의존성), `BookController.java`

## Refactoring: DTO와 ResponseEntity 변경 전/후

## New Features: Validation, 공통 예외 처리, Pagination/Sorting, Swagger

## Testing & Deployment: results/ 스크린샷 6장 이상 및 배포 URL

## Weekly Review: 새롭게 배운 내용 3가지, 어려웠던 점과 해결 방법, AI 활용 내용 및 느낀 점