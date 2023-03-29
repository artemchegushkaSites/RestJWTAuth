package ru.artemchegushka.restjwtauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.artemchegushka.restjwtauth.dto.AuthResponse;
import ru.artemchegushka.restjwtauth.dto.AuthService;
import ru.artemchegushka.restjwtauth.dto.LoginRequest;
import ru.artemchegushka.restjwtauth.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
     return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest loginRequest) {
      return ResponseEntity.ok(authService.login(loginRequest));
    }
}
