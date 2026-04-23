package com.example.javamvcthymeleaf.controller.dto;

import com.example.javamvcthymeleaf.model.Proposta;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class PropostaForm {

    private Integer idProposta;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
    private String descricao;

    @NotNull(message = "A data de submissão é obrigatória")
    @PastOrPresent(message = "A data de submissão não pode ser futura")
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
