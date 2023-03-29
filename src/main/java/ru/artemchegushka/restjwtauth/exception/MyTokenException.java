package ru.artemchegushka.restjwtauth.exception;
// Можно было бы использовать и UserException, но что бы не запутаться, я сделал отдельный класс
public class MyTokenException extends RuntimeException {
    public MyTokenException(String s) {
        super(s);
    }
}
