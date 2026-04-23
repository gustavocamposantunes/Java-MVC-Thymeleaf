package com.example.javamvcthymeleaf.config;

import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.repository.CursoRepository;
import com.example.javamvcthymeleaf.repository.PropostaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseSeederTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private PropostaRepository propostaRepository;

    private DatabaseSeeder seeder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        seeder = new DatabaseSeeder(cursoRepository, propostaRepository);
    }

    @Test
    void runDeveSemearQuandoRepositoriosEstaoVazios() throws Exception {
        when(cursoRepository.count()).thenReturn(0L);
        when(propostaRepository.count()).thenReturn(0L);

        seeder.run();

        ArgumentCaptor<List<Curso>> cursosCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Proposta>> propostasCaptor = ArgumentCaptor.forClass(List.class);
        verify(cursoRepository).saveAll(cursosCaptor.capture());
        verify(propostaRepository).saveAll(propostasCaptor.capture());
        assertEquals(3, cursosCaptor.getValue().size());
        assertEquals(3, propostasCaptor.getValue().size());
    }

    @Test
    void runNaoDeveSemearQuandoJaExistemDados() throws Exception {
        when(cursoRepository.count()).thenReturn(1L);
        when(propostaRepository.count()).thenReturn(1L);

        seeder.run();

        verify(cursoRepository, never()).saveAll(anyList());
        verify(propostaRepository, never()).saveAll(anyList());
    }
}
