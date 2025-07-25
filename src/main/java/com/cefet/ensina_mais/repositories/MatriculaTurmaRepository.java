package com.cefet.ensina_mais.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cefet.ensina_mais.entities.MatriculaTurma;

public interface MatriculaTurmaRepository extends JpaRepository<MatriculaTurma, Long> {
    
    public List<MatriculaTurma> findByMatriculaId(Long id);

    public List<MatriculaTurma> findByTurmaId(Long id);
}
