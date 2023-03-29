package ru.artemchegushka.restjwtauth.exception;

import java.time.LocalDateTime;

public class UserExceptionHandler {

    private String message;
    private LocalDateTime localDateTime;

    public UserExceptionHandler(String message, LocalDateTime localDateTime) {
        this.message = message;
        this.localDateTime = localDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
