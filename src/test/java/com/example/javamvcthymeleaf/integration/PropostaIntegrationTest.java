package com.example.javamvcthymeleaf.integration;

import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.repository.PropostaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = "app.seed.enabled=false")
@AutoConfigureMockMvc
class PropostaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropostaRepository propostaRepository;

    @BeforeEach
    void limparBase() {
        propostaRepository.deleteAll();
    }

    @Test
    void listarPropostasDeveExibirVoltarParaHome() throws Exception {
        mockMvc.perform(get("/propostas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Voltar para home")))
                .andExpect(content().string(containsString("href=\"/\"")));
    }

    @Test
    void criarPropostaDevePersistirComAnexo() throws Exception {
        byte[] arquivo = "conteudo-anexo".getBytes();
        MockMultipartFile anexo = new MockMultipartFile("anexo", "arquivo.txt", "text/plain", arquivo);

        mockMvc.perform(multipart("/propostas")
                        .file(anexo)
                        .param("titulo", "Proposta A")
                        .param("descricao", "Descrição A")
                        .param("dataSubmissao", LocalDate.now().toString())
                        .param("status", "aprovado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/propostas"));

        assertEquals(1, propostaRepository.count());
        Proposta proposta = propostaRepository.findAll().get(0);
        assertEquals("Proposta A", proposta.getTitulo());
        assertEquals("Descrição A", proposta.getDescricao());
        assertEquals("aprovado", proposta.getStatus());
        assertArrayEquals(arquivo, proposta.getAnexos());
    }

    @Test
    void criarPropostaComStatusInvalidoDeveVoltarFormulario() throws Exception {
        mockMvc.perform(post("/propostas")
                        .param("titulo", "Proposta")
                        .param("descricao", "Descrição")
                        .param("dataSubmissao", LocalDate.now().toString())
                        .param("status", "status-invalido"))
                .andExpect(status().isOk())
                .andExpect(view().name("propostas/form"))
                .andExpect(model().attributeHasFieldErrors("propostaForm", "status"));
    }

    @Test
    void atualizarPropostaDevePersistirAlteracoes() throws Exception {
        Proposta proposta = new Proposta();
        proposta.setTitulo("Título antigo");
        proposta.setDescricao("Descrição antiga");
        proposta.setDataSubmissao(LocalDate.now());
        proposta.setStatus("aguardando aprovação");
        proposta = propostaRepository.save(proposta);

        mockMvc.perform(post("/propostas/{id}", proposta.getIdProposta())
                        .param("titulo", "Título novo")
                        .param("descricao", "Descrição nova")
                        .param("dataSubmissao", LocalDate.now().toString())
                        .param("status", "aprovado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/propostas"));

        Proposta atualizada = propostaRepository.findById(proposta.getIdProposta()).orElseThrow();
        assertEquals("Título novo", atualizada.getTitulo());
        assertEquals("Descrição nova", atualizada.getDescricao());
        assertEquals("aprovado", atualizada.getStatus());
    }

    @Test
    void baixarAnexoDeveRetornarArquivo() throws Exception {
        byte[] arquivo = "abc123".getBytes();
        Proposta proposta = new Proposta();
        proposta.setTitulo("Com anexo");
        proposta.setDescricao("Descrição");
        proposta.setDataSubmissao(LocalDate.now());
        proposta.setStatus("aprovado");
        proposta.setAnexos(arquivo);
        proposta = propostaRepository.save(proposta);

        mockMvc.perform(get("/propostas/{id}/anexo", proposta.getIdProposta()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().string("Content-Disposition", containsString("proposta-" + proposta.getIdProposta() + "-anexo.bin")))
                .andExpect(content().bytes(arquivo));
    }

    @Test
    void excluirPropostaDeveRemoverRegistro() throws Exception {
        Proposta proposta = new Proposta();
        proposta.setTitulo("Excluir");
        proposta.setDescricao("Descrição");
        proposta.setDataSubmissao(LocalDate.now());
        proposta.setStatus("rejeitado");
        proposta = propostaRepository.save(proposta);

        mockMvc.perform(post("/propostas/{id}/excluir", proposta.getIdProposta()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/propostas"));

        assertFalse(propostaRepository.findById(proposta.getIdProposta()).isPresent());
    }
}
