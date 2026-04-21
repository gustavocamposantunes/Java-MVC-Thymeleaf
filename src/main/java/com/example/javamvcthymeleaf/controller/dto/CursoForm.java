package com.example.javamvcthymeleaf.controller.dto;

import com.example.javamvcthymeleaf.model.Curso;

import javax.validation.constraints.NotBlank;

public class CursoForm {

    private Integer idCurso;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    public static CursoForm fromEntity(Curso curso) {
        CursoForm form = new CursoForm();
        form.setIdCurso(curso.getIdCurso());
        form.setNome(curso.getNome());
        form.setDescricao(curso.getDescricao());
        return form;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}