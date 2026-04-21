package com.example.javamvcthymeleaf.repository;

import com.example.javamvcthymeleaf.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
}