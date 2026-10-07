package br.pucminas.moedaestudantil.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.config.RegrasTransferencia;
import br.pucminas.moedaestudantil.controller.form.TransferenciaForm;
import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.model.StatusTransferencia;
import br.pucminas.moedaestudantil.model.TransferenciaMoedas;
import br.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.pucminas.moedaestudantil.repository.TransferenciaMoedasRepository;

/**
 * Transferência de moedas entre alunos com aprovação de professor (UC14–UC17).
 * Os critérios de solicitação estão em {@link #solicitar} e os de decisão em {@link #aprovar} e
 * {@link #rejeitar}; os limites numéricos vêm de {@link RegrasTransferencia}.
 */
@Service
@Transactional
public class TransferenciaService {

    /** Status que consomem o limite mensal: o que já saiu do saldo e não voltou. */
    private static final EnumSet<StatusTransferencia> CONTAM_NO_LIMITE =
            EnumSet.of(StatusTransferencia.PENDENTE, StatusTransferencia.APROVADA);

    private final TransferenciaMoedasRepository transferenciaRepository;
    private final AlunoService alunoService;
    private final ProfessorRepository professorRepository;
    private final RegrasTransferencia regras;

    public TransferenciaService(TransferenciaMoedasRepository transferenciaRepository, AlunoService alunoService,
                                ProfessorRepository professorRepository, RegrasTransferencia regras) {
        this.transferenciaRepository = transferenciaRepository;
        this.alunoService = alunoService;
        this.professorRepository = professorRepository;
        this.regras = regras;
    }

    @Transactional(readOnly = true)
    public List<TransferenciaMoedas> listar() {
        return transferenciaRepository.findAllByOrderByDataHoraDesc();
    }

    @Transactional(readOnly = true)
    public List<TransferenciaMoedas> listarPendentes() {
        return transferenciaRepository.findByStatusOrderByDataHoraAsc(StatusTransferencia.PENDENTE);
    }

    @Transactional(readOnly = true)
    public List<TransferenciaMoedas> doAluno(Aluno aluno) {
        return transferenciaRepository.doAluno(aluno);
    }

    @Transactional(readOnly = true)
    public TransferenciaMoedas buscar(Long id) {
        return transferenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transferência", id));
    }

    /** Professores que podem avaliar a transferência: os da instituição dos alunos (RN18). */
    @Transactional(readOnly = true)
    public List<Professor> avaliadoresPossiveis(TransferenciaMoedas transferencia) {
        return professorRepository.findByInstituicaoOrderByNomeAsc(transferencia.getRemetente().getInstituicao());
    }

    /** Quanto o aluno ainda pode transferir no mês corrente (RN15). */
    @Transactional(readOnly = true)
    public int disponivelNoMes(Aluno aluno) {
        int usado = transferenciaRepository.somaEnviadaDesde(aluno, CONTAM_NO_LIMITE, inicioDoMes());
        return Math.max(0, regras.limiteMensal() - usado);
    }

    /**
     * Registra a solicitação e reserva o valor no saldo do remetente.
     * Critérios verificados, nesta ordem:
     * <ol>
     *   <li>RN13 — remetente e destinatário são alunos diferentes da mesma instituição;</li>
     *   <li>RN17 — motivo com o tamanho mínimo;</li>
     *   <li>RN14 — valor entre 1 e o máximo por transferência;</li>
     *   <li>RN17 — remetente abaixo do limite de solicitações pendentes;</li>
     *   <li>RN15 — valor dentro do limite mensal;</li>
     *   <li>RN14 — saldo suficiente.</li>
     * </ol>
     */
    public TransferenciaMoedas solicitar(TransferenciaForm form) {
        Aluno remetente = alunoService.buscar(form.getRemetenteId());
        Aluno destinatario = alunoService.buscar(form.getDestinatarioId());
        int valor = form.getValor();
        String motivo = form.getMotivo() == null ? "" : form.getMotivo().trim();

        if (Objects.equals(remetente.getId(), destinatario.getId())) {
            throw new RegraNegocioException("destinatarioId", "O aluno não pode transferir moedas para si mesmo.");
        }
        if (!Objects.equals(remetente.getInstituicao().getId(), destinatario.getInstituicao().getId())) {
            throw new RegraNegocioException("destinatarioId",
                    "Só é possível transferir para alunos da mesma instituição de ensino.");
        }
        if (motivo.length() < regras.tamanhoMinimoMotivo()) {
            throw new RegraNegocioException("motivo",
                    "Descreva o motivo com pelo menos " + regras.tamanhoMinimoMotivo() + " caracteres.");
        }
        if (valor < 1 || valor > regras.valorMaximo()) {
            throw new RegraNegocioException("valor",
                    "O valor deve estar entre 1 e " + regras.valorMaximo() + " moedas por transferência.");
        }
        if (transferenciaRepository.countByRemetenteAndStatus(remetente, StatusTransferencia.PENDENTE)
                >= regras.maximoPendentes()) {
            throw new RegraNegocioException("O aluno já tem " + regras.maximoPendentes()
                    + " transferências aguardando aprovação. Aguarde a decisão ou cancele uma delas.");
        }
        int disponivel = disponivelNoMes(remetente);
        if (valor > disponivel) {
            throw new RegraNegocioException("valor", "Limite mensal de " + regras.limiteMensal()
                    + " moedas transferidas atingido. Disponível neste mês: " + disponivel + " moedas.");
        }
        if (valor > remetente.getSaldoMoedas()) {
            throw new RegraNegocioException("valor",
                    "Saldo insuficiente: o aluno tem " + remetente.getSaldoMoedas() + " moedas.");
        }
        return transferenciaRepository.save(new TransferenciaMoedas(remetente, destinatario, valor, motivo));
    }

    /** Aprovação por um professor da instituição dos alunos (RN18). */
    public TransferenciaMoedas aprovar(Long id, Long professorId) {
        TransferenciaMoedas transferencia = buscar(id);
        Professor professor = avaliador(transferencia, professorId);
        try {
            transferencia.aprovar(professor);
        } catch (IllegalStateException e) {
            throw new RegraNegocioException(e.getMessage());
        }
        return transferencia;
    }

    /** Rejeição por um professor da instituição dos alunos, com justificativa obrigatória (RN18). */
    public TransferenciaMoedas rejeitar(Long id, Long professorId, String justificativa) {
        TransferenciaMoedas transferencia = buscar(id);
        Professor professor = avaliador(transferencia, professorId);
        String texto = justificativa == null ? "" : justificativa.trim();
        if (texto.length() < regras.tamanhoMinimoMotivo()) {
            throw new RegraNegocioException("justificativa",
                    "Justifique a rejeição com pelo menos " + regras.tamanhoMinimoMotivo() + " caracteres.");
        }
        try {
            transferencia.rejeitar(professor, texto);
        } catch (IllegalStateException e) {
            throw new RegraNegocioException(e.getMessage());
        }
        return transferencia;
    }

    /** Somente o remetente cancela, e somente enquanto a transferência estiver pendente (RN19). */
    public TransferenciaMoedas cancelar(Long id, Long alunoId) {
        TransferenciaMoedas transferencia = buscar(id);
        if (!Objects.equals(transferencia.getRemetente().getId(), alunoId)) {
            throw new RegraNegocioException("Somente o aluno remetente pode cancelar a transferência.");
        }
        try {
            transferencia.cancelar();
        } catch (IllegalStateException e) {
            throw new RegraNegocioException(e.getMessage());
        }
        return transferencia;
    }

    /**
     * Expira as solicitações pendentes há mais do que o prazo e devolve o valor aos remetentes (RN20).
     *
     * @return quantidade de transferências expiradas
     */
    public int expirarVencidas(LocalDateTime agora) {
        LocalDateTime limite = agora.minusDays(regras.prazoAprovacaoDias());
        List<TransferenciaMoedas> vencidas =
                transferenciaRepository.findByStatusAndDataHoraBefore(StatusTransferencia.PENDENTE, limite);
        vencidas.forEach(TransferenciaMoedas::expirar);
        return vencidas.size();
    }

    public RegrasTransferencia getRegras() {
        return regras;
    }

    private Professor avaliador(TransferenciaMoedas transferencia, Long professorId) {
        if (professorId == null) {
            throw new RegraNegocioException("professorId", "Selecione o professor responsável pela decisão.");
        }
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RegraNegocioException("professorId", "Professor inexistente."));
        if (!Objects.equals(professor.getInstituicao().getId(), transferencia.getRemetente().getInstituicao().getId())) {
            throw new RegraNegocioException("professorId",
                    "Somente professores da instituição dos alunos podem avaliar esta transferência.");
        }
        return professor;
    }

    private static LocalDateTime inicioDoMes() {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay();
    }
}
