package com.cefet.ensina_mais.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.cefet.ensina_mais.dto.AvaliacaoDTO;
import com.cefet.ensina_mais.entities.Avaliacao;
import com.cefet.ensina_mais.entities.MatriculaTurma;
import com.cefet.ensina_mais.entities.Nota;
import com.cefet.ensina_mais.entities.Turma;
import com.cefet.ensina_mais.repositories.AvaliacaoRepository;
import com.cefet.ensina_mais.repositories.MatriculaTurmaRepository;
import com.cefet.ensina_mais.repositories.NotaRepository;
import com.cefet.ensina_mais.repositories.TurmaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AvaliacaoService {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private MatriculaTurmaRepository matriculaTurmaRepository;

    public List<AvaliacaoDTO> findAll() {
        List<Avaliacao> lista = avaliacaoRepository.findAll();
        return lista.stream().map(AvaliacaoDTO::new).toList();
    }

    public AvaliacaoDTO findById(Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com ID: " + id));
        return new AvaliacaoDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoDTO insert(AvaliacaoDTO dto) {
        if (dto.getData() == null)
            throw new IllegalArgumentException("A data da avaliação não pode ser vazia.");

        if (!StringUtils.hasText(dto.getDescricao()))
            throw new IllegalArgumentException("A descrição da avaliação não pode ser vazia.");

        if (dto.getNotaMaxima() == null)
            throw new IllegalArgumentException("A nota máxima da avaliação não pode ser vazia.");

        Turma turma = turmaRepository.findById(dto.getIdTurma())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + dto.getIdTurma()));

        // 1. Criar a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setData(dto.getData());
        avaliacao.setDescricao(dto.getDescricao());
        avaliacao.setNotaMaxima(dto.getNotaMaxima());
        avaliacao.setTurma(turma);
        avaliacao = avaliacaoRepository.save(avaliacao);

        // 2. Criar notas automaticamente para todos os alunos da turma
        criarNotasParaAlunosDaTurma(avaliacao);

        return new AvaliacaoDTO(avaliacao);
    }

    /**
     * Cria automaticamente notas (vazias/null) para todos os alunos matriculados na turma
     * quando uma nova avaliação é criada
     */
    private void criarNotasParaAlunosDaTurma(Avaliacao avaliacao) {
        // Buscar todas as matrículas-turmas da turma desta avaliação
        List<MatriculaTurma> matriculasTurma = matriculaTurmaRepository.findByTurmaId(avaliacao.getTurma().getId());
        
        for (MatriculaTurma matriculaTurma : matriculasTurma) {
            // Verificar se já existe nota para esta avaliação e esta matrícula-turma
            List<Nota> notasExistentes = notaRepository.findByAvaliacaoIdAndMatriculaTurmaId(
                avaliacao.getId(), 
                matriculaTurma.getId()
            );
            
            // Se não existe nota, criar uma nova (inicialmente null)
            if (notasExistentes.isEmpty()) {
                Nota nota = new Nota();
                nota.setNota(null); // Nota inicialmente vazia
                nota.setAvaliacao(avaliacao);
                nota.setMatriculaTurma(matriculaTurma);
                notaRepository.save(nota);
            }
        }
    }

    public AvaliacaoDTO update(Long id, AvaliacaoDTO dto) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com ID: " + id));

        if (dto.getData() != null)
            avaliacao.setData(dto.getData());

        if (StringUtils.hasText(dto.getDescricao()))
            avaliacao.setDescricao(dto.getDescricao());

        if (dto.getNotaMaxima() != null)
            avaliacao.setNotaMaxima(dto.getNotaMaxima());

        if (dto.getIdTurma() != null) {
            Turma turma = turmaRepository.findById(dto.getIdTurma())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + dto.getIdTurma()));
            avaliacao.setTurma(turma);
        }

        avaliacao = avaliacaoRepository.save(avaliacao);
        return new AvaliacaoDTO(avaliacao);
    }

    @Transactional
    public void delete(Long id) {
        if (!avaliacaoRepository.existsById(id))
            throw new EntityNotFoundException("Avaliação não encontrada com ID: " + id);

        List<Nota> notas = notaRepository.findByAvaliacaoId(id);
        notaRepository.deleteAll(notas);
        
        avaliacaoRepository.deleteById(id);
    }
}
