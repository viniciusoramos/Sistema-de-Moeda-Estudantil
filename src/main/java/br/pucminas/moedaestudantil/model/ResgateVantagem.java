package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/** Troca de moedas por uma vantagem; o código do cupom vai nos e-mails ao aluno e ao parceiro (RN10). */
@Entity
@Table(name = "resgate_vantagem")
@PrimaryKeyJoinColumn(name = "transacao_id")
public class ResgateVantagem extends Transacao {

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vantagem_id", nullable = false)
    private Vantagem vantagem;

    @Column(name = "codigo_cupom", nullable = false, unique = true, length = 12)
    private String codigoCupom;

    protected ResgateVantagem() {
    }

    public ResgateVantagem(Aluno aluno, Vantagem vantagem, String codigoCupom) {
        super(vantagem.getCustoMoedas());
        this.aluno = aluno;
        this.vantagem = vantagem;
        this.codigoCupom = codigoCupom;
    }

    @Override
    public String getDescricao() {
        return "Resgate de \"" + vantagem.getTitulo() + "\" (" + vantagem.getEmpresa().getNome() + ")";
    }

    public Aluno getAluno() { return aluno; }
    public Vantagem getVantagem() { return vantagem; }
    public String getCodigoCupom() { return codigoCupom; }
}
