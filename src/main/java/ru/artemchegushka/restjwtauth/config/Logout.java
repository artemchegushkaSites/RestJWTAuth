package ru.artemchegushka.restjwtauth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.artemchegushka.restjwtauth.repository.TokenRepo;

// Главное это этот класс и настройки в security
@Component
public class Logout implements LogoutHandler {
    private final TokenRepo repo;
@Autowired
    public Logout(TokenRepo repo) {
        this.repo = repo;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//   Здесь еще должно быть куча проверок, но суть кода передана
    String token = request.getHeader("Authorization").substring(7);
    repo.deleteTokenByToken(token);
        SecurityContextHolder.clearContext();

    }
}
