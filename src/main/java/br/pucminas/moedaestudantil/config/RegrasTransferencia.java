package br.pucminas.moedaestudantil.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Limites das transferências entre alunos (RN14, RN15, RN17, RN20).
 * Podem ser ajustados em application.properties com o prefixo {@code moeda.transferencia}.
 *
 * @param valorMaximo         maior valor de uma única transferência
 * @param limiteMensal        soma máxima transferida por um aluno no mês (pendentes + aprovadas)
 * @param maximoPendentes     quantas solicitações um aluno pode ter aguardando decisão ao mesmo tempo
 * @param prazoAprovacaoDias  dias até uma solicitação pendente expirar
 * @param tamanhoMinimoMotivo menor quantidade de caracteres aceita no motivo
 */
@ConfigurationProperties(prefix = "moeda.transferencia")
public record RegrasTransferencia(
        @DefaultValue("100") int valorMaximo,
        @DefaultValue("300") int limiteMensal,
        @DefaultValue("3") int maximoPendentes,
        @DefaultValue("7") int prazoAprovacaoDias,
        @DefaultValue("10") int tamanhoMinimoMotivo) {
}
