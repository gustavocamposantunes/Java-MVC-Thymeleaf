package com.example.javamvcthymeleaf.controller.dto;

import com.example.javamvcthymeleaf.model.Curso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CursoFormTest {

    @Test
    void fromEntityDeveMapearCampos() {
        Curso curso = new Curso();
        curso.setIdCurso(11);
        curso.setNome("Java");
        curso.setDescricao("Spring");

        CursoForm form = CursoForm.fromEntity(curso);

        assertEquals(11, form.getIdCurso());
        assertEquals("Java", form.getNome());
        assertEquals("Spring", form.getDescricao());
    }
}
