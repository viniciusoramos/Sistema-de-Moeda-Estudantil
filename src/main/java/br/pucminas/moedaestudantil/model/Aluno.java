package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluno")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Aluno extends Usuario {

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 20)
    private String rg;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false, length = 80)
    private String curso;

    @Column(name = "saldo_moedas", nullable = false)
    private int saldoMoedas;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;

    protected Aluno() {
    }

    public Aluno(String nome, String email, String senhaHash, String cpf, String rg,
                 Endereco endereco, String curso, InstituicaoEnsino instituicao) {
        super(nome, email, senhaHash);
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
        this.curso = curso;
        this.instituicao = instituicao;
        this.saldoMoedas = 0;
    }

    /** Crédito recebido de um professor (RN05). */
    public void receberMoedas(int valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo.");
        }
        saldoMoedas += valor;
    }

    /** Débito pelo resgate de uma vantagem (RN08). */
    public void debitarMoedas(int valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo.");
        }
        if (valor > saldoMoedas) {
            throw new SaldoInsuficienteException(saldoMoedas, valor);
        }
        saldoMoedas -= valor;
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.ALUNO; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public int getSaldoMoedas() { return saldoMoedas; }
    public InstituicaoEnsino getInstituicao() { return instituicao; }
    public void setInstituicao(InstituicaoEnsino instituicao) { this.instituicao = instituicao; }
}
