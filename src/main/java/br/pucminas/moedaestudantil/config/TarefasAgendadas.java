package br.pucminas.moedaestudantil.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.pucminas.moedaestudantil.service.TransferenciaService;

/** Tarefas executadas pelo ator "Tempo" do diagrama de casos de uso. */
@Component
public class TarefasAgendadas {

    private static final Logger log = LoggerFactory.getLogger(TarefasAgendadas.class);

    private final TransferenciaService transferenciaService;

    public TarefasAgendadas(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    /** UC17 — a cada hora, expira as transferências pendentes além do prazo (RN20). */
    @Scheduled(cron = "0 0 * * * *")
    public void expirarTransferencias() {
        int expiradas = transferenciaService.expirarVencidas(LocalDateTime.now());
        if (expiradas > 0) {
            log.info("{} transferência(s) expirada(s) e estornada(s).", expiradas);
        }
    }
}
