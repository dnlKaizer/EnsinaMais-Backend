package com.cefet.ensina_mais.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_matricula_turma", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"matricula_id", "turma_id"}))
public class MatriculaTurma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aprovado = 1, Reprovado = 0, Em Andamento = 2?
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private SituacaoMatricula situacao;

    @Column(nullable = true)
    private Double notaFinal;

    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    public MatriculaTurma() {
    }

    public MatriculaTurma(Long id, SituacaoMatricula situacao, Double notaFinal, Matricula matricula, Turma turma) {
        this.id = id;
        this.situacao = situacao;
        this.notaFinal = notaFinal;
        this.matricula = matricula;
        this.turma = turma;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoMatricula getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMatricula situacao) {
        this.situacao = situacao;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MatriculaTurma other = (MatriculaTurma) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
