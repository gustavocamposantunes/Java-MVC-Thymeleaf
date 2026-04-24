package com.example.javamvcthymeleaf.service;

import com.example.javamvcthymeleaf.model.Usuario;
import com.example.javamvcthymeleaf.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String username, String senha) {
        if (username == null || username.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByUsername(username.trim())
                .filter(usuario -> senha.equals(usuario.getSenha()));
    }
}
