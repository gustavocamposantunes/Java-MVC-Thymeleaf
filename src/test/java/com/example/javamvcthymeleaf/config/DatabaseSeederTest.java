package com.example.javamvcthymeleaf.config;

import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.model.Usuario;
import com.example.javamvcthymeleaf.repository.CursoRepository;
import com.example.javamvcthymeleaf.repository.PropostaRepository;
import com.example.javamvcthymeleaf.repository.UsuarioRepository;
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

    @Mock
    private UsuarioRepository usuarioRepository;

    private DatabaseSeeder seeder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        seeder = new DatabaseSeeder(cursoRepository, propostaRepository, usuarioRepository);
    }

    @Test
    void runDeveSemearQuandoRepositoriosEstaoVazios() throws Exception {
        when(cursoRepository.count()).thenReturn(0L);
        when(propostaRepository.count()).thenReturn(0L);
        when(usuarioRepository.existsByUsername("admin")).thenReturn(false);

        seeder.run();

        ArgumentCaptor<List<Curso>> cursosCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Proposta>> propostasCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(cursoRepository).saveAll(cursosCaptor.capture());
        verify(propostaRepository).saveAll(propostasCaptor.capture());
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals(3, cursosCaptor.getValue().size());
        assertEquals(3, propostasCaptor.getValue().size());
        assertEquals("admin", usuarioCaptor.getValue().getUsername());
        assertEquals("admin", usuarioCaptor.getValue().getSenha());
    }

    @Test
    void runNaoDeveSemearQuandoJaExistemDados() throws Exception {
        when(cursoRepository.count()).thenReturn(1L);
        when(propostaRepository.count()).thenReturn(1L);
        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        seeder.run();

        verify(cursoRepository, never()).saveAll(anyList());
        verify(propostaRepository, never()).saveAll(anyList());
        verify(usuarioRepository, never()).save(org.mockito.ArgumentMatchers.any(Usuario.class));
    }
}
