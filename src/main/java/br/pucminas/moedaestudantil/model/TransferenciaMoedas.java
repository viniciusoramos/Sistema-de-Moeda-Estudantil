package br.pucminas.moedaestudantil.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Transferência de moedas entre dois alunos, sujeita à aprovação de um professor (RN13–RN20).
 *
 * <p>O valor sai do saldo do remetente já na solicitação (reserva), para que ele não possa gastar
 * as mesmas moedas em outro resgate ou transferência enquanto aguarda a decisão. Na aprovação o
 * valor é creditado ao destinatário; na rejeição, no cancelamento ou na expiração, volta ao remetente.
 */
@Entity
@Table(name = "transferencia_moedas")
@PrimaryKeyJoinColumn(name = "transacao_id")
public class TransferenciaMoedas extends Transacao {

    @ManyToOne(optional = false)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Aluno remetente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Aluno destinatario;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusTransferencia status;

    @ManyToOne
    @JoinColumn(name = "avaliador_id")
    private Professor avaliador;

    @Column(name = "data_decisao")
    private LocalDateTime dataDecisao;

    @Column(length = 500)
    private String justificativa;

    protected TransferenciaMoedas() {
    }

    /** Cria a solicitação e reserva o valor, debitando-o do remetente. */
    public TransferenciaMoedas(Aluno remetente, Aluno destinatario, int valor, String motivo) {
        super(valor);
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.motivo = motivo;
        this.status = StatusTransferencia.PENDENTE;
        remetente.debitarMoedas(valor);
    }

    /** Aprovação por professor: credita o destinatário (RN18). */
    public void aprovar(Professor professor) {
        exigirPendente();
        destinatario.receberMoedas(getValor());
        decidir(StatusTransferencia.APROVADA, professor, null);
    }

    /** Rejeição por professor, com justificativa: estorna o remetente (RN18). */
    public void rejeitar(Professor professor, String justificativa) {
        exigirPendente();
        remetente.receberMoedas(getValor());
        decidir(StatusTransferencia.REJEITADA, professor, justificativa);
    }

    /** Desistência do remetente: estorna o valor (RN19). */
    public void cancelar() {
        exigirPendente();
        remetente.receberMoedas(getValor());
        decidir(StatusTransferencia.CANCELADA, null, null);
    }

    /** Fim do prazo sem decisão: estorna o valor (RN20). */
    public void expirar() {
        exigirPendente();
        remetente.receberMoedas(getValor());
        decidir(StatusTransferencia.EXPIRADA, null, "Prazo de aprovação esgotado.");
    }

    public boolean isPendente() {
        return status == StatusTransferencia.PENDENTE;
    }

    private void exigirPendente() {
        if (!isPendente()) {
            throw new IllegalStateException(
                    "A transferência já foi encerrada (" + status.getRotulo().toLowerCase() + ").");
        }
    }

    private void decidir(StatusTransferencia novoStatus, Professor professor, String justificativa) {
        this.status = novoStatus;
        this.avaliador = professor;
        this.justificativa = justificativa;
        this.dataDecisao = LocalDateTime.now();
    }

    @Override
    public String getDescricao() {
        return "Transferência de " + remetente.getNome() + " para " + destinatario.getNome()
                + " (" + status.getRotulo().toLowerCase() + "): " + motivo;
    }

    public Aluno getRemetente() { return remetente; }
    public Aluno getDestinatario() { return destinatario; }
    public String getMotivo() { return motivo; }
    public StatusTransferencia getStatus() { return status; }
    public Professor getAvaliador() { return avaliador; }
    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public String getJustificativa() { return justificativa; }
}
