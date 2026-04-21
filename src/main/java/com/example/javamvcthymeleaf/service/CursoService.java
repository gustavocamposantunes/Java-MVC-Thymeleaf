package com.example.javamvcthymeleaf.service;

import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> listarTodas() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> buscarPorId(Integer id) {
        return cursoRepository.findById(id);
    }

    public Curso salvar(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void excluir(Integer id) {
        cursoRepository.deleteById(id);
    }
}