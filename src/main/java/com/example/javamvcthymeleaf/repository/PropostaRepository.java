package com.example.javamvcthymeleaf.repository;

import com.example.javamvcthymeleaf.model.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropostaRepository extends JpaRepository<Proposta, Integer> {
}
