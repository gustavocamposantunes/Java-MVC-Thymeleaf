package com.example.javamvcthymeleaf.service;

import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.repository.CursoRepository;
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

class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    private CursoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new CursoService(cursoRepository);
    }

    @Test
    void listarTodasDeveRetornarCursosDoRepositorio() {
        List<Curso> esperado = Arrays.asList(new Curso(), new Curso());
        when(cursoRepository.findAll()).thenReturn(esperado);

        List<Curso> resultado = service.listarTodas();

        assertEquals(2, resultado.size());
        verify(cursoRepository).findAll();
    }

    @Test
    void buscarPorIdDeveDelegarAoRepositorio() {
        Curso curso = new Curso();
        when(cursoRepository.findById(1)).thenReturn(Optional.of(curso));

        Optional<Curso> resultado = service.buscarPorId(1);

        assertTrue(resultado.isPresent());
        verify(cursoRepository).findById(1);
    }

    @Test
    void salvarDeveDelegarAoRepositorio() {
        Curso curso = new Curso();
        when(cursoRepository.save(curso)).thenReturn(curso);

        Curso resultado = service.salvar(curso);

        assertEquals(curso, resultado);
        verify(cursoRepository).save(curso);
    }

    @Test
    void excluirDeveDelegarAoRepositorio() {
        service.excluir(10);

        verify(cursoRepository).deleteById(10);
    }
}
