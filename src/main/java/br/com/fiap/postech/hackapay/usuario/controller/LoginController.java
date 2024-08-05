package br.com.fiap.postech.hackapay.usuario.controller;

import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.security.Token;
import br.com.fiap.postech.hackapay.usuario.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Service
@RequestMapping("/autenticacao")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "realiza o login do user")
    @PostMapping()
    public ResponseEntity<?> login(@Valid @RequestBody User user) throws Exception {
        try {
            Token token = userService.login(user);
            return new ResponseEntity<>(token, HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
