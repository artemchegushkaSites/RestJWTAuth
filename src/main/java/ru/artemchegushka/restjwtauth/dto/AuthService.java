package ru.artemchegushka.restjwtauth.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.artemchegushka.restjwtauth.config.JwtService;
import ru.artemchegushka.restjwtauth.entity.MyUser;
import ru.artemchegushka.restjwtauth.exception.UserException;
import ru.artemchegushka.restjwtauth.repository.TokenRepo;
import ru.artemchegushka.restjwtauth.service.MyAuthProvider;
import ru.artemchegushka.restjwtauth.service.MyUserDetailsService;


@Service
public class AuthService {
    private final MyUserDetailsService service;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final MyAuthProvider provider;
    private final TokenRepo tokenRepo;

    @Autowired
    public AuthService(MyUserDetailsService service, BCryptPasswordEncoder encoder, JwtService jwtService, MyAuthProvider provider, TokenRepo tokenRepo) {
        this.service = service;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.provider = provider;
        this.tokenRepo = tokenRepo;
    }
// Логика регистрации
    public AuthResponse register(RegisterRequest registerRequest) {
        MyUser user = MyUser
                .builder()
                .username(registerRequest.getUsername())
                .password(encoder.encode(registerRequest.getPassword()))
                .age(registerRequest.getAge())
                .role("ROLE_USER")
                .build();
        service.register(user);
        String jwtToken = jwtService.generateToken(user.getUsername());
        tokenRepo.saveToken(user.getUsername(),jwtToken);
        return AuthResponse.builder().token(jwtToken).build();

    }

//   Логика логина
    public AuthResponse login(LoginRequest loginRequest) {
        MyUser user = service.findByUserName(loginRequest.getUsername())
                .orElseThrow(()->new UserException("Пользователь с таким логином не найден!"));
//      эта строка логинит пользователя
        provider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String jwtToken = jwtService.generateToken(user.getUsername());
        tokenRepo.deleteToken(user.getUsername());
        tokenRepo.saveToken(user.getUsername(),jwtToken);
        return AuthResponse.builder().token(jwtToken).build();

    }
}
