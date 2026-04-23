package com.example.javamvcthymeleaf.controller.dto;

import com.example.javamvcthymeleaf.model.Proposta;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropostaFormTest {

    @Test
    void fromEntityDeveMapearCampos() {
        LocalDate data = LocalDate.now();
        Proposta proposta = new Proposta();
        proposta.setIdProposta(13);
        proposta.setTitulo("Título");
        proposta.setDescricao("Descrição");
        proposta.setDataSubmissao(data);
        proposta.setStatus("aprovado");

        PropostaForm form = PropostaForm.fromEntity(proposta);

        assertEquals(13, form.getIdProposta());
        assertEquals("Título", form.getTitulo());
        assertEquals("Descrição", form.getDescricao());
        assertEquals(data, form.getDataSubmissao());
        assertEquals("aprovado", form.getStatus());
    }
}
