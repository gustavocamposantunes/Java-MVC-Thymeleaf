package com.example.javamvcthymeleaf.config;

import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.model.Usuario;
import com.example.javamvcthymeleaf.repository.CursoRepository;
import com.example.javamvcthymeleaf.repository.PropostaRepository;
import com.example.javamvcthymeleaf.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final CursoRepository cursoRepository;
    private final PropostaRepository propostaRepository;
    private final UsuarioRepository usuarioRepository;

    public DatabaseSeeder(CursoRepository cursoRepository,
                          PropostaRepository propostaRepository,
                          UsuarioRepository usuarioRepository) {
        this.cursoRepository = cursoRepository;
        this.propostaRepository = propostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdminUser();
        seedCursos();
        seedPropostas();
    }

    private void seedAdminUser() {
        if (usuarioRepository.existsByUsername("admin")) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setSenha("admin");
        admin.setPerfil("ADMIN");

        usuarioRepository.save(admin);
    }

    private void seedCursos() {
        if (cursoRepository.count() > 0) {
            return;
        }

        Curso cursoJava = new Curso();
        cursoJava.setNome("Java Web com Spring");
        cursoJava.setDescricao("Fundamentos de Spring Boot, MVC e Thymeleaf para aplicações web.");

        Curso cursoDados = new Curso();
        cursoDados.setNome("Banco de Dados Relacionais");
        cursoDados.setDescricao("Modelagem de dados, SQL e boas práticas com PostgreSQL.");

        Curso cursoDocker = new Curso();
        cursoDocker.setNome("Docker para Desenvolvedores");
        cursoDocker.setDescricao("Construção de imagens, execução de containers e deploy com Docker.");

        cursoRepository.saveAll(Arrays.asList(cursoJava, cursoDados, cursoDocker));
    }

    private void seedPropostas() {
        if (propostaRepository.count() > 0) {
            return;
        }

        Proposta proposta1 = new Proposta();
        proposta1.setTitulo("Portal de Cursos Online");
        proposta1.setDescricao("Criar um portal web para cadastro de cursos e submissão de propostas.");
        proposta1.setDataSubmissao(LocalDate.now().minusDays(7));
        proposta1.setStatus("aguardando aprovação");

        Proposta proposta2 = new Proposta();
        proposta2.setTitulo("Sistema de Gestão Acadêmica");
        proposta2.setDescricao("Desenvolver um módulo para acompanhamento de matrículas e turmas.");
        proposta2.setDataSubmissao(LocalDate.now().minusDays(3));
        proposta2.setStatus("aprovado");

        Proposta proposta3 = new Proposta();
        proposta3.setTitulo("Plataforma de Trilhas de Aprendizagem");
        proposta3.setDescricao("Implementar trilhas por nível e relatórios de progresso para alunos.");
        proposta3.setDataSubmissao(LocalDate.now());
        proposta3.setStatus("reijeitado");

        propostaRepository.saveAll(Arrays.asList(proposta1, proposta2, proposta3));
    }
}
