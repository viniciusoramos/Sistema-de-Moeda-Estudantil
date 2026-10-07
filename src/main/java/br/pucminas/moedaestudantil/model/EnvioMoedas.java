package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/** Envio de moedas de um professor a um aluno, com motivo obrigatório (RN04). */
@Entity
@Table(name = "envio_moedas")
@PrimaryKeyJoinColumn(name = "transacao_id")
public class EnvioMoedas extends Transacao {

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false, length = 500)
    private String motivo;

    protected EnvioMoedas() {
    }

    public EnvioMoedas(Professor professor, Aluno aluno, int valor, String motivo) {
        super(valor);
        this.professor = professor;
        this.aluno = aluno;
        this.motivo = motivo;
    }

    @Override
    public String getDescricao() {
        return "Envio de " + professor.getNome() + " para " + aluno.getNome() + ": " + motivo;
    }

    public Professor getProfessor() { return professor; }
    public Aluno getAluno() { return aluno; }
    public String getMotivo() { return motivo; }
}
