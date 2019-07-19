package com.github.alex353cay.protei.test.controller.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such user")
public class UserNotFoundException extends RuntimeException {
}
