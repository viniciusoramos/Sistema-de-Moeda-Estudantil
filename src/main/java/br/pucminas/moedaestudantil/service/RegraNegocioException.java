package br.pucminas.moedaestudantil.service;

/** Violação de regra de negócio, exibida ao usuário na própria tela. */
public class RegraNegocioException extends RuntimeException {

    /** Campo do formulário associado ao erro; nulo quando o erro é geral. */
    private final String campo;

    public RegraNegocioException(String mensagem) {
        this(null, mensagem);
    }

    public RegraNegocioException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
