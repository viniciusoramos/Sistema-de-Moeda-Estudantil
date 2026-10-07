package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/** Professor pré-cadastrado pela instituição parceira (RN02). */
@Entity
@Table(name = "professor")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Professor extends Usuario {

    /** Moedas creditadas a cada semestre (RN03). */
    public static final int CREDITO_SEMESTRAL = 1000;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 80)
    private String departamento;

    @Column(name = "saldo_moedas", nullable = false)
    private int saldoMoedas;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;

    protected Professor() {
    }

    public Professor(String nome, String email, String senhaHash, String cpf,
                     String departamento, InstituicaoEnsino instituicao) {
        super(nome, email, senhaHash);
        this.cpf = cpf;
        this.departamento = departamento;
        this.instituicao = instituicao;
        this.saldoMoedas = 0;
    }

    /** Soma as 1.000 moedas do semestre ao saldo corrente; o saldo é acumulável (RN03). */
    public void receberCreditoSemestral() {
        saldoMoedas += CREDITO_SEMESTRAL;
    }

    /** Debita o valor enviado a um aluno; exige saldo suficiente (RN04). */
    public void debitarEnvio(int valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo.");
        }
        if (valor > saldoMoedas) {
            throw new SaldoInsuficienteException(saldoMoedas, valor);
        }
        saldoMoedas -= valor;
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.PROFESSOR; }

    public String getCpf() { return cpf; }
    public String getDepartamento() { return departamento; }
    public int getSaldoMoedas() { return saldoMoedas; }
    public InstituicaoEnsino getInstituicao() { return instituicao; }
}
