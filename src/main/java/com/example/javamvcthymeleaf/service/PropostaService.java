package com.example.javamvcthymeleaf.service;

import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.repository.PropostaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropostaService {

    private final PropostaRepository propostaRepository;

    public PropostaService(PropostaRepository propostaRepository) {
        this.propostaRepository = propostaRepository;
    }

    public List<Proposta> listarTodas() {
        return propostaRepository.findAll();
    }

    public Optional<Proposta> buscarPorId(Integer id) {
        return propostaRepository.findById(id);
    }

    public Proposta salvar(Proposta proposta) {
        return propostaRepository.save(proposta);
    }

    public void excluir(Integer id) {
        propostaRepository.deleteById(id);
    }
}
