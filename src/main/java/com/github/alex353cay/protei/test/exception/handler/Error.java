package com.github.alex353cay.protei.test.exception.handler;

import org.springframework.http.HttpStatus;

class Error {
    private final HttpStatus status;
    private final String message;
    private static final String contact = "mailto:alex353cay@gmail.com?subject=Вопрос по protei-test";

    Error(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getContact() {
        return contact;
    }
}
