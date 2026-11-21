package edu.handong.week12.exception;

import edu.handong.week12.exception.constants.ExceptionCode;

public class CustomException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public CustomException(final ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public CustomException(final ExceptionCode exceptionCode, final Exception exception) {
        super(exception.getMessage(), exception);
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
