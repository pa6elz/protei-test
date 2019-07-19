package com.github.alex353cay.protei.test.exception.handler;

import com.github.alex353cay.protei.test.controller.user.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Error> handleAllUnhandledExceptions(Exception ex, WebRequest request) {
        Error error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "Please refer to API docs (https://github.com/Alex353CAY/protei-test/blob/master/README.md)");
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Error> handleUserNotFoundException(Exception e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND, "User not found"), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error apiError = new Error(HttpStatus.BAD_REQUEST, "Please refer to API docs (https://github.com/Alex353CAY/protei-test/blob/master/README.md)");
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
