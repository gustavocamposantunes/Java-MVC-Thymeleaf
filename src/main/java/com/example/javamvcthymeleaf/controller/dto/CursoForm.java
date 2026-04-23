package com.example.javamvcthymeleaf.controller.dto;

import com.example.javamvcthymeleaf.model.Curso;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CursoForm {

    private Integer idCurso;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
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