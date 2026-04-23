package com.example.javamvcthymeleaf.controller;

import com.example.javamvcthymeleaf.controller.dto.PropostaForm;
import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.service.PropostaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PropostaControllerTest {

    @Mock
    private PropostaService propostaService;

    private PropostaController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new PropostaController(propostaService);
    }

    @Test
    void listarDeveAdicionarPropostasEDevolverView() {
        Model model = new ExtendedModelMap();
        when(propostaService.listarTodas()).thenReturn(Arrays.asList(new Proposta(), new Proposta()));

        String view = controller.listar(model);

        assertEquals("propostas/lista", view);
        assertNotNull(model.getAttribute("propostas"));
    }

    @Test
    void novaDevePrepararFormulario() {
        Model model = new ExtendedModelMap();

        String view = controller.nova(model);

        assertEquals("propostas/form", view);
        assertNotNull(model.getAttribute("propostaForm"));
        assertNotNull(model.getAttribute("statusOptions"));
        assertEquals(false, model.getAttribute("modoEdicao"));
    }

    @Test
    void criarComStatusInvalidoDeveRetornarFormulario() throws IOException {
        PropostaForm form = propostaValida();
        form.setStatus("invalido");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "propostaForm");
        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.criar(form, bindingResult, model, redirectAttributes);

        assertEquals("propostas/form", view);
        assertEquals(false, model.getAttribute("modoEdicao"));
        assertEquals("Corrija os campos destacados.", model.getAttribute("mensagemErro"));
        verify(propostaService, never()).salvar(any(Proposta.class));
    }

    @Test
    void criarComAnexoMaiorQue10mbDeveRetornarFormulario() throws IOException {
        PropostaForm form = propostaValida();
        byte[] bytes = new byte[(10 * 1024 * 1024) + 1];
        form.setAnexo(new MockMultipartFile("anexo", "arquivo.bin", "application/octet-stream", bytes));
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "propostaForm");
        Model model = new ExtendedModelMap();

        String view = controller.criar(form, bindingResult, model, new RedirectAttributesModelMap());

        assertEquals("propostas/form", view);
        verify(propostaService, never()).salvar(any(Proposta.class));
    }

    @Test
    void criarSemErrosDeveSalvarERedirecionar() throws IOException {
        PropostaForm form = propostaValida();
        byte[] arquivo = "conteudo".getBytes();
        form.setAnexo(new MockMultipartFile("anexo", "arquivo.bin", "application/octet-stream", arquivo));
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "propostaForm");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.criar(form, bindingResult, new ExtendedModelMap(), redirectAttributes);

        assertEquals("redirect:/propostas", view);
        assertEquals("Proposta cadastrada com sucesso.", redirectAttributes.getFlashAttributes().get("mensagemSucesso"));
        verify(propostaService).salvar(any(Proposta.class));
    }

    @Test
    void editarComIdInexistenteDeveLancarNotFound() {
        when(propostaService.buscarPorId(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.editar(1, new ExtendedModelMap()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void editarComIdExistenteDeveCarregarFormulario() {
        Proposta proposta = new Proposta();
        proposta.setIdProposta(9);
        proposta.setTitulo("Título");
        proposta.setDescricao("Descrição");
        proposta.setDataSubmissao(LocalDate.now());
        proposta.setStatus("aprovado");
        when(propostaService.buscarPorId(9)).thenReturn(Optional.of(proposta));

        Model model = new ExtendedModelMap();

        String view = controller.editar(9, model);

        assertEquals("propostas/form", view);
        assertNotNull(model.getAttribute("propostaForm"));
        assertNotNull(model.getAttribute("statusOptions"));
        assertEquals(true, model.getAttribute("modoEdicao"));
    }

    @Test
    void atualizarComIdInexistenteDeveLancarNotFound() {
        when(propostaService.buscarPorId(1)).thenReturn(Optional.empty());
        PropostaForm form = propostaValida();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "propostaForm");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.atualizar(1, form, bindingResult, new ExtendedModelMap(), new RedirectAttributesModelMap()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void atualizarComErroDeValidacaoDeveRetornarFormulario() throws IOException {
        when(propostaService.buscarPorId(7)).thenReturn(Optional.of(new Proposta()));

        PropostaForm form = propostaValida();
        form.setStatus("status-invalido");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "propostaForm");
        Model model = new ExtendedModelMap();

        String view = controller.atualizar(7, form, bindingResult, model, new RedirectAttributesModelMap());

        assertEquals("propostas/form", view);
        assertEquals(7, form.getIdProposta());
        assertEquals(true, model.getAttribute("modoEdicao"));
        assertEquals("Corrija os campos destacados.", model.getAttribute("mensagemErro"));
        verify(propostaService, never()).salvar(any(Proposta.class));
    }

    @Test
    void atualizarSemErrosDeveSalvarERedirecionar() throws IOException {
        Proposta proposta = new Proposta();
        when(propostaService.buscarPorId(7)).thenReturn(Optional.of(proposta));

        PropostaForm form = propostaValida();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "propostaForm");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.atualizar(7, form, bindingResult, new ExtendedModelMap(), redirectAttributes);

        assertEquals("redirect:/propostas", view);
        assertEquals("Proposta atualizada com sucesso.", redirectAttributes.getFlashAttributes().get("mensagemSucesso"));
        verify(propostaService).salvar(proposta);
    }

    @Test
    void excluirComIdInexistenteDeveRetornarMensagemErro() {
        when(propostaService.buscarPorId(1)).thenReturn(Optional.empty());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.excluir(1, redirectAttributes);

        assertEquals("redirect:/propostas", view);
        assertEquals("Proposta não encontrada para exclusão.", redirectAttributes.getFlashAttributes().get("mensagemErro"));
        verify(propostaService, never()).excluir(1);
    }

    @Test
    void excluirComIdExistenteDeveExcluirERedirecionar() {
        when(propostaService.buscarPorId(1)).thenReturn(Optional.of(new Proposta()));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.excluir(1, redirectAttributes);

        assertEquals("redirect:/propostas", view);
        assertEquals("Proposta excluída com sucesso.", redirectAttributes.getFlashAttributes().get("mensagemSucesso"));
        verify(propostaService).excluir(1);
    }

    @Test
    void baixarAnexoComIdInexistenteDeveLancarNotFound() {
        when(propostaService.buscarPorId(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.baixarAnexo(1));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void baixarAnexoSemConteudoDeveLancarNotFound() {
        Proposta proposta = new Proposta();
        proposta.setAnexos(new byte[0]);
        when(propostaService.buscarPorId(2)).thenReturn(Optional.of(proposta));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.baixarAnexo(2));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void baixarAnexoComConteudoDeveRetornarArquivo() {
        byte[] arquivo = "abc".getBytes();
        Proposta proposta = new Proposta();
        proposta.setAnexos(arquivo);
        when(propostaService.buscarPorId(3)).thenReturn(Optional.of(proposta));

        ResponseEntity<ByteArrayResource> response = controller.baixarAnexo(3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"proposta-3-anexo.bin\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(arquivo.length, response.getHeaders().getContentLength());
        assertNotNull(response.getBody());
        assertArrayEquals(arquivo, response.getBody().getByteArray());
    }

    private PropostaForm propostaValida() {
        PropostaForm form = new PropostaForm();
        form.setTitulo("Título");
        form.setDescricao("Descrição");
        form.setDataSubmissao(LocalDate.now());
        form.setStatus("aprovado");
        return form;
    }
}
