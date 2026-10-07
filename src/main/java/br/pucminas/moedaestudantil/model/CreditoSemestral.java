package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Crédito de 1.000 moedas feito ao professor no início de cada semestre (RN03). */
@Entity
@Table(name = "credito_semestral",
       uniqueConstraints = @UniqueConstraint(columnNames = {"professor_id", "semestre"}))
@PrimaryKeyJoinColumn(name = "transacao_id")
public class CreditoSemestral extends Transacao {

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    /** Semestre no formato "AAAA/S", por exemplo "2026/2". */
    @Column(nullable = false, length = 6)
    private String semestre;

    protected CreditoSemestral() {
    }

    public CreditoSemestral(Professor professor, String semestre) {
        super(Professor.CREDITO_SEMESTRAL);
        this.professor = professor;
        this.semestre = semestre;
    }

    @Override
    public String getDescricao() {
        return "Crédito semestral " + semestre;
    }

    public Professor getProfessor() { return professor; }
    public String getSemestre() { return semestre; }
}
