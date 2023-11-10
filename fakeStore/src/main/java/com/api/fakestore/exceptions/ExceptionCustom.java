package com.api.fakestore.exceptions;


import org.springframework.http.HttpStatus;

public class ExceptionCustom extends RuntimeException {

    private HttpStatus httpStatus;

    public ExceptionCustom(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}