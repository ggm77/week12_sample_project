package edu.handong.week12.exception.handler;

import edu.handong.week12.exception.CustomException;
import edu.handong.week12.exception.constants.ExceptionCode;
import edu.handong.week12.exception.response.ExceptionResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // @Valid 검증 실패 (제목/저자 빈 값, 음수 가격) → 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        final String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        final ExceptionResponse response = new ExceptionResponse(ExceptionCode.INVALID_REQUEST, message);

        return ResponseEntity.status(ExceptionCode.INVALID_REQUEST.getHttpStatus()).body(response);
    }

    // JSON 문법 오류, 타입 불일치 (예: price에 문자열) → 400
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        final ExceptionResponse response = new ExceptionResponse(ExceptionCode.INVALID_JSON);

        return ResponseEntity.status(ExceptionCode.INVALID_JSON.getHttpStatus()).body(response);
    }

    // 경로 변수 타입 불일치 (예: /api/books/abc) → 400
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            final TypeMismatchException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        final ExceptionResponse response = new ExceptionResponse(ExceptionCode.INVALID_PARAMETER);

        return ResponseEntity.status(ExceptionCode.INVALID_PARAMETER.getHttpStatus()).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(final CustomException ex) {

        if (ex.getCause() != null) {
            System.out.println("EXCEPTION CODE " + ex.getExceptionCode().name() + ", message: "+ex.getMessage());
        }

        final ExceptionResponse response = new ExceptionResponse(ex.getExceptionCode());

        return ResponseEntity.status(ex.getExceptionCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllException(final Exception ex) {
        System.out.println("INTERNAL SERVER ERROR: "+  ex.getMessage());
        System.out.println(ex);

        final ExceptionResponse response = new ExceptionResponse(ExceptionCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.internalServerError().body(response);
    }
}
