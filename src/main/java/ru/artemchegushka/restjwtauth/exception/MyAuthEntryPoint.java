package ru.artemchegushka.restjwtauth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
// Класс нужен для того, что бы перехватывать событие, когда не авторизованный или не зарегистрированный пользователь
// пытается получить доступ, мы тогда выдаем сообщение
@Component
public class MyAuthEntryPoint implements AuthenticationEntryPoint {

    private final FilterExceptHandler handler;

    @Autowired
    public MyAuthEntryPoint(FilterExceptHandler handler) {
        this.handler = handler;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        handler.handleAuthException(response, HttpServletResponse.SC_UNAUTHORIZED,"Вы не авторизованы, необходимо войти или зарегистрироваться.");


    }
}
