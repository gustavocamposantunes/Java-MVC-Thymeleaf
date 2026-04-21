package com.example.javamvcthymeleaf.controller.dto;

import com.example.javamvcthymeleaf.model.Proposta;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class PropostaForm {

    private Integer idProposta;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "A data de submissão é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataSubmissao;

    @NotBlank(message = "O status é obrigatório")
    private String status;

    private MultipartFile anexo;

    public static PropostaForm fromEntity(Proposta proposta) {
        PropostaForm form = new PropostaForm();
        form.setIdProposta(proposta.getIdProposta());
        form.setTitulo(proposta.getTitulo());
        form.setDescricao(proposta.getDescricao());
        form.setDataSubmissao(proposta.getDataSubmissao());
        form.setStatus(proposta.getStatus());
        return form;
    }

    public Integer getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(Integer idProposta) {
        this.idProposta = idProposta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataSubmissao() {
        return dataSubmissao;
    }

    public void setDataSubmissao(LocalDate dataSubmissao) {
        this.dataSubmissao = dataSubmissao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MultipartFile getAnexo() {
        return anexo;
    }

    public void setAnexo(MultipartFile anexo) {
        this.anexo = anexo;
    }
}
