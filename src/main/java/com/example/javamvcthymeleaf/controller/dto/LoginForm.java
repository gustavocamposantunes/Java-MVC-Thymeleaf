package com.example.javamvcthymeleaf.controller.dto;

import javax.validation.constraints.NotBlank;

public class LoginForm {

    @NotBlank(message = "Informe o usuário")
    private String username;

    @NotBlank(message = "Informe a senha")
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
