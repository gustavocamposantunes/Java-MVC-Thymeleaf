package com.example.javamvcthymeleaf.service;

import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.repository.PropostaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PropostaServiceTest {

    @Mock
    private PropostaRepository propostaRepository;

    private PropostaService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new PropostaService(propostaRepository);
    }

    @Test
    void listarTodasDeveRetornarPropostasDoRepositorio() {
        List<Proposta> esperado = Arrays.asList(new Proposta(), new Proposta());
        when(propostaRepository.findAll()).thenReturn(esperado);

        List<Proposta> resultado = service.listarTodas();

        assertEquals(2, resultado.size());
        verify(propostaRepository).findAll();
    }

    @Test
    void buscarPorIdDeveDelegarAoRepositorio() {
        Proposta proposta = new Proposta();
        when(propostaRepository.findById(1)).thenReturn(Optional.of(proposta));

        Optional<Proposta> resultado = service.buscarPorId(1);

        assertTrue(resultado.isPresent());
        verify(propostaRepository).findById(1);
    }

    @Test
    void salvarDeveDelegarAoRepositorio() {
        Proposta proposta = new Proposta();
        when(propostaRepository.save(proposta)).thenReturn(proposta);

        Proposta resultado = service.salvar(proposta);

        assertEquals(proposta, resultado);
        verify(propostaRepository).save(proposta);
    }

    @Test
    void excluirDeveDelegarAoRepositorio() {
        service.excluir(10);

        verify(propostaRepository).deleteById(10);
    }
}
