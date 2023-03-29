package ru.artemchegushka.restjwtauth.exception;

public class UserException extends RuntimeException{
    public UserException(String message) {
        super(message);
    }
}
