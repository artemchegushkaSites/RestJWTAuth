package ru.artemchegushka.restjwtauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.artemchegushka.restjwtauth.exception.UserException;
import ru.artemchegushka.restjwtauth.exception.UserExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity<UserExceptionHandler> exception(UserException userException) {
        UserExceptionHandler handler = new UserExceptionHandler(userException.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(handler);
    }
}
