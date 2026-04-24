package com.example.javamvcthymeleaf.controller;

import com.example.javamvcthymeleaf.config.AuthSession;
import com.example.javamvcthymeleaf.model.Usuario;
import com.example.javamvcthymeleaf.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body, HttpSession session) {
        Optional<Usuario> usuario = authService.autenticar(body.getUsername(), body.getSenha());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais inválidas"));
        }

        session.setAttribute(AuthSession.USER_ID, usuario.get().getIdUsuario());
        session.setAttribute(AuthSession.USERNAME, usuario.get().getUsername());
        session.setAttribute(AuthSession.ROLE, usuario.get().getPerfil());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Login realizado com sucesso");
        payload.put("username", usuario.get().getUsername());
        payload.put("role", usuario.get().getPerfil());
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userId = session.getAttribute(AuthSession.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Não autenticado"));
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", userId);
        payload.put("username", session.getAttribute(AuthSession.USERNAME));
        payload.put("role", session.getAttribute(AuthSession.ROLE));

        return ResponseEntity.ok(payload);
    }

    public static class LoginRequest {

        @NotBlank
        private String username;

        @NotBlank
        private String senha;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }
}
