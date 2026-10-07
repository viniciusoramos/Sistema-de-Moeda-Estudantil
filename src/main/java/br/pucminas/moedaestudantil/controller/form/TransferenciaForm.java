package br.pucminas.moedaestudantil.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Solicitação de transferência de moedas entre alunos (UC14). */
public class TransferenciaForm {

    /** Na Lab03S03 virá do aluno autenticado; até lá é escolhido na tela. */
    @NotNull(message = "Selecione o aluno remetente.")
    private Long remetenteId;

    @NotNull(message = "Selecione o aluno destinatário.")
    private Long destinatarioId;

    @NotNull(message = "Informe o valor.")
    @Positive(message = "O valor deve ser maior que zero.")
    private Integer valor;

    @NotBlank(message = "Informe o motivo da transferência.")
    @Size(max = 500)
    private String motivo;

    public Long getRemetenteId() { return remetenteId; }
    public void setRemetenteId(Long remetenteId) { this.remetenteId = remetenteId; }
    public Long getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }
    public Integer getValor() { return valor; }
    public void setValor(Integer valor) { this.valor = valor; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
