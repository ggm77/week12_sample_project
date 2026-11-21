package edu.handong.week12.exception.response;

import edu.handong.week12.exception.constants.ExceptionCode;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ExceptionResponse {
    private final Instant timestamp;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ExceptionResponse(final ExceptionCode exceptionCode) {
        this.timestamp = Instant.now();
        this.httpStatus = exceptionCode.getHttpStatus();
        this.code = exceptionCode.name();
        this.message = exceptionCode.getMessage();
    }

    public ExceptionResponse(final ExceptionCode exceptionCode, final String message) {
        this.timestamp = Instant.now();
        this.httpStatus = exceptionCode.getHttpStatus();
        this.code = exceptionCode.name();
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
