package com.example.javamvcthymeleaf.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.seed.enabled=false")
@AutoConfigureMockMvc
class PropostaPageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarPropostasDeveExibirLinkVoltarParaHome() throws Exception {
        mockMvc.perform(get("/propostas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Voltar para home")))
                .andExpect(content().string(containsString("href=\"/\"")));
    }
}
