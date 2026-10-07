package br.pucminas.moedaestudantil.model;

/** Ciclo de vida de uma transferência entre alunos (RN16). */
public enum StatusTransferencia {

    /** Aguardando decisão de um professor; o valor fica reservado (debitado do remetente). */
    PENDENTE("Pendente"),
    /** Aprovada por um professor; o valor foi creditado ao destinatário. */
    APROVADA("Aprovada"),
    /** Recusada por um professor; o valor voltou ao remetente. */
    REJEITADA("Rejeitada"),
    /** Desistência do remetente antes da decisão; o valor voltou a ele. */
    CANCELADA("Cancelada"),
    /** Ficou pendente além do prazo; o valor voltou ao remetente. */
    EXPIRADA("Expirada");

    private final String rotulo;

    StatusTransferencia(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
