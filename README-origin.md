# Week 12 Sample Project - Book REST API (학생용 Starter)
기본 CRUD가 구현되어 있습니다. **해답 코드(DTO/Validation/Exception/Pagination/Swagger)는 제공되지 않습니다.**

## 실행
- Java 17 필요
- Gradle 8.x 설치 후 `gradle bootRun` (프로젝트에는 공식 Gradle Wrapper 바이너리가 포함되지 않았습니다.)
- 기본값은 학습 편의상 메모리 H2 DB입니다. 재시작 시 데이터가 초기화됩니다.
- MariaDB 실습: `docker compose up -d` 후 아래 환경 변수를 설정하여 실행하세요.
  - `SPRING_PROFILES_ACTIVE=mariadb`
  - `DB_URL=jdbc:mariadb://localhost:3307/week12`
  - `DB_USERNAME=week12`, `DB_PASSWORD=week12_local_only`
  - 처음 DB 생성 시에만 `DDL_AUTO=update` (이후 `validate` 권장)
- **Render 배포**에서는 DB_URL/DB_USERNAME/DB_PASSWORD와 `SPRING_PROFILES_ACTIVE=mariadb`, `DDL_AUTO=validate`를 설정하고, 외부 MariaDB 접근을 사전에 확인하세요.

## API
- GET `/api/books`, GET `/api/books/{id}`
- POST `/api/books`, PUT `/api/books/{id}`, DELETE `/api/books/{id}`

```bash
curl http://localhost:8080/api/books
curl -X POST http://localhost:8080/api/books -H 'Content-Type: application/json' -d '{"title":"Clean Code","author":"Martin","price":35000}'
```

## TODO (STEP 3~7)
1. `BookRequest`, `BookResponse` DTO 만들기
2. Controller와 Service의 요청/응답을 DTO 중심으로 개선하고 `ResponseEntity` 사용
3. Bean Validation 적용
4. 전역 예외 처리: 400/404 구조 통일
5. `Pageable` / `Sort` 구현 (`GET /api/books?page=0&size=5&sort=price,desc`)
6. Swagger UI / OpenAPI 문서화

> 테스트 결과는 `results/`에 저장하고 README에 자기 말로 분석 결과를 기록하세요.
