package com.api.fakestore.exceptions;

import java.io.Serializable;


public class ExceptionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private String details;

    public ExceptionResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
