package com.example.javamvcthymeleaf.controller;

import com.example.javamvcthymeleaf.controller.dto.CursoForm;
import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.service.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CursoControllerTest {

    @Mock
    private CursoService cursoService;

    private CursoController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new CursoController(cursoService);
    }

    @Test
    void listarDeveAdicionarCursosEDevolverView() {
        Model model = new ExtendedModelMap();
        when(cursoService.listarTodas()).thenReturn(Arrays.asList(new Curso(), new Curso()));

        String view = controller.listar(model);

        assertEquals("cursos/lista", view);
        assertNotNull(model.getAttribute("cursos"));
    }

    @Test
    void novoDevePrepararFormulario() {
        Model model = new ExtendedModelMap();

        String view = controller.novo(model);

        assertEquals("cursos/form", view);
        assertNotNull(model.getAttribute("cursoForm"));
        assertEquals(false, model.getAttribute("modoEdicao"));
    }

    @Test
    void criarComErroDeValidacaoDeveRetornarFormulario() {
        CursoForm form = new CursoForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "cursoForm");
        bindingResult.rejectValue("nome", "NotBlank", "erro");
        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.criar(form, bindingResult, model, redirectAttributes);

        assertEquals("cursos/form", view);
        assertEquals(false, model.getAttribute("modoEdicao"));
        assertEquals("Corrija os campos destacados.", model.getAttribute("mensagemErro"));
        verify(cursoService, never()).salvar(any(Curso.class));
    }

    @Test
    void criarSemErrosDeveSalvarERedirecionar() {
        CursoForm form = new CursoForm();
        form.setNome("Java");
        form.setDescricao("Curso");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "cursoForm");
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.criar(form, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/cursos", view);
        assertEquals("Curso cadastrado com sucesso.", redirectAttributes.getFlashAttributes().get("mensagemSucesso"));
        verify(cursoService).salvar(any(Curso.class));
    }

    @Test
    void editarComIdInexistenteDeveLancarNotFound() {
        when(cursoService.buscarPorId(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.editar(1, new ExtendedModelMap()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void editarComIdExistenteDeveCarregarFormulario() {
        Curso curso = new Curso();
        curso.setIdCurso(10);
        curso.setNome("Spring");
        curso.setDescricao("MVC");
        when(cursoService.buscarPorId(10)).thenReturn(Optional.of(curso));
        Model model = new ExtendedModelMap();

        String view = controller.editar(10, model);

        assertEquals("cursos/form", view);
        assertNotNull(model.getAttribute("cursoForm"));
        assertEquals(true, model.getAttribute("modoEdicao"));
    }

    @Test
    void atualizarComIdInexistenteDeveLancarNotFound() {
        when(cursoService.buscarPorId(1)).thenReturn(Optional.empty());
        CursoForm form = new CursoForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "cursoForm");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.atualizar(1, form, bindingResult, new ExtendedModelMap(), new RedirectAttributesModelMap()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void atualizarComErroDeValidacaoDeveRetornarFormulario() {
        Curso curso = new Curso();
        when(cursoService.buscarPorId(5)).thenReturn(Optional.of(curso));

        CursoForm form = new CursoForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "cursoForm");
        bindingResult.rejectValue("descricao", "NotBlank", "erro");
        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.atualizar(5, form, bindingResult, model, redirectAttributes);

        assertEquals("cursos/form", view);
        assertEquals(5, form.getIdCurso());
        assertEquals(true, model.getAttribute("modoEdicao"));
        assertEquals("Corrija os campos destacados.", model.getAttribute("mensagemErro"));
        verify(cursoService, never()).salvar(any(Curso.class));
    }

    @Test
    void atualizarSemErrosDeveSalvarERedirecionar() {
        Curso curso = new Curso();
        when(cursoService.buscarPorId(5)).thenReturn(Optional.of(curso));

        CursoForm form = new CursoForm();
        form.setNome("Novo Nome");
        form.setDescricao("Nova Descrição");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "cursoForm");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.atualizar(5, form, bindingResult, new ExtendedModelMap(), redirectAttributes);

        assertEquals("redirect:/cursos", view);
        assertEquals("Curso atualizado com sucesso.", redirectAttributes.getFlashAttributes().get("mensagemSucesso"));
        verify(cursoService).salvar(curso);
    }

    @Test
    void excluirComIdInexistenteDeveRetornarMensagemErro() {
        when(cursoService.buscarPorId(1)).thenReturn(Optional.empty());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.excluir(1, redirectAttributes);

        assertEquals("redirect:/cursos", view);
        assertEquals("Curso não encontrado para exclusão.", redirectAttributes.getFlashAttributes().get("mensagemErro"));
        verify(cursoService, never()).excluir(1);
    }

    @Test
    void excluirComIdExistenteDeveExcluirERedirecionar() {
        when(cursoService.buscarPorId(1)).thenReturn(Optional.of(new Curso()));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.excluir(1, redirectAttributes);

        assertEquals("redirect:/cursos", view);
        assertEquals("Curso excluído com sucesso.", redirectAttributes.getFlashAttributes().get("mensagemSucesso"));
        verify(cursoService).excluir(1);
    }
}
