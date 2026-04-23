package com.example.javamvcthymeleaf.integration;

import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = "app.seed.enabled=false")
@AutoConfigureMockMvc
class CursoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CursoRepository cursoRepository;

    @BeforeEach
    void limparBase() {
        cursoRepository.deleteAll();
    }

    @Test
    void listarCursosDeveExibirVoltarParaHome() throws Exception {
        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Voltar para home")))
                .andExpect(content().string(containsString("href=\"/\"")));
    }

    @Test
    void criarCursoDevePersistirERedirecionar() throws Exception {
        mockMvc.perform(post("/cursos")
                        .param("nome", "Java")
                        .param("descricao", "Curso de Java"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cursos"));

        assertEquals(1, cursoRepository.count());
        Curso curso = cursoRepository.findAll().get(0);
        assertEquals("Java", curso.getNome());
        assertEquals("Curso de Java", curso.getDescricao());
    }

    @Test
    void criarCursoInvalidoDeveVoltarFormularioComErros() throws Exception {
        mockMvc.perform(post("/cursos")
                        .param("nome", "")
                        .param("descricao", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cursos/form"))
                .andExpect(model().attributeHasFieldErrors("cursoForm", "nome", "descricao"));
    }

    @Test
    void atualizarCursoDevePersistirAlteracoes() throws Exception {
        Curso curso = new Curso();
        curso.setNome("Nome antigo");
        curso.setDescricao("Descrição antiga");
        curso = cursoRepository.save(curso);

        mockMvc.perform(post("/cursos/{id}", curso.getIdCurso())
                        .param("nome", "Nome novo")
                        .param("descricao", "Descrição nova"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cursos"));

        Curso atualizado = cursoRepository.findById(curso.getIdCurso()).orElseThrow();
        assertEquals("Nome novo", atualizado.getNome());
        assertEquals("Descrição nova", atualizado.getDescricao());
    }

    @Test
    void excluirCursoDeveRemoverRegistro() throws Exception {
        Curso curso = new Curso();
        curso.setNome("Excluir");
        curso.setDescricao("Curso para excluir");
        curso = cursoRepository.save(curso);

        mockMvc.perform(post("/cursos/{id}/excluir", curso.getIdCurso()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cursos"));

        assertFalse(cursoRepository.findById(curso.getIdCurso()).isPresent());
        assertTrue(cursoRepository.findAll().isEmpty());
    }
}
