package br.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.controller.form.TransferenciaForm;
import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.model.StatusTransferencia;
import br.pucminas.moedaestudantil.model.TransferenciaMoedas;
import br.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;

@SpringBootTest
@Transactional
class TransferenciaServiceTest {

    @Autowired TransferenciaService service;
    @Autowired AlunoService alunoService;
    @Autowired InstituicaoEnsinoService instituicaoService;
    @Autowired ProfessorRepository professorRepository;
    @Autowired TransacaoRepository transacaoRepository;

    Aluno ana;
    Aluno bruno;
    Aluno deOutraInstituicao;
    Professor professor;
    Professor professorDeOutraInstituicao;

    @BeforeEach
    void setUp() {
        List<InstituicaoEnsino> instituicoes = instituicaoService.listar();
        InstituicaoEnsino inst = instituicoes.get(0);
        InstituicaoEnsino outra = instituicoes.get(1);

        ana = alunoService.cadastrar(DadosDeTeste.aluno("Ana", "ana@t.com", DadosDeTeste.cpf("321654987"), inst.getId()));
        bruno = alunoService.cadastrar(DadosDeTeste.aluno("Bruno", "bruno@t.com", DadosDeTeste.cpf("321654988"), inst.getId()));
        deOutraInstituicao = alunoService.cadastrar(
                DadosDeTeste.aluno("Caio", "caio@t.com", DadosDeTeste.cpf("321654989"), outra.getId()));
        ana.receberMoedas(500);

        professor = professorRepository.findByInstituicaoOrderByNomeAsc(inst).get(0);
        professorDeOutraInstituicao = professorRepository.findByInstituicaoOrderByNomeAsc(outra).get(0);
    }

    @Test
    void solicitacaoReservaOValorAteADecisao() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));

        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.PENDENTE);
        assertThat(ana.getSaldoMoedas()).isEqualTo(450);
        assertThat(bruno.getSaldoMoedas()).isZero();
    }

    @Test
    void aprovacaoCreditaODestinatario() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));

        service.aprovar(t.getId(), professor.getId());

        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.APROVADA);
        assertThat(t.getAvaliador()).isEqualTo(professor);
        assertThat(t.getDataDecisao()).isNotNull();
        assertThat(ana.getSaldoMoedas()).isEqualTo(450);
        assertThat(bruno.getSaldoMoedas()).isEqualTo(50);
    }

    @Test
    void rejeicaoExigeJustificativaEDevolveOValor() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));

        assertThatThrownBy(() -> service.rejeitar(t.getId(), professor.getId(), "não"))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("justificativa");

        service.rejeitar(t.getId(), professor.getId(), "Motivo não condiz com a atividade.");
        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.REJEITADA);
        assertThat(ana.getSaldoMoedas()).isEqualTo(500);
        assertThat(bruno.getSaldoMoedas()).isZero();
    }

    @Test
    void somenteORemetenteCancelaEOValorVolta() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));

        assertThatThrownBy(() -> service.cancelar(t.getId(), bruno.getId()))
                .isInstanceOf(RegraNegocioException.class);

        service.cancelar(t.getId(), ana.getId());
        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.CANCELADA);
        assertThat(ana.getSaldoMoedas()).isEqualTo(500);
    }

    @Test
    void naoTransfereParaSiMesmo() {
        assertThatThrownBy(() -> service.solicitar(form(ana, ana, 10)))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("destinatarioId");
    }

    @Test
    void naoTransfereParaAlunoDeOutraInstituicao() {
        assertThatThrownBy(() -> service.solicitar(form(ana, deOutraInstituicao, 10)))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("destinatarioId");
    }

    @Test
    void respeitaOValorMaximoPorTransferencia() {
        assertThatThrownBy(() -> service.solicitar(form(ana, bruno, 101)))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("valor");
    }

    @Test
    void exigeMotivoComTamanhoMinimo() {
        TransferenciaForm f = form(ana, bruno, 10);
        f.setMotivo("valeu");

        assertThatThrownBy(() -> service.solicitar(f))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("motivo");
    }

    @Test
    void exigeSaldoSuficiente() {
        assertThatThrownBy(() -> service.solicitar(form(bruno, ana, 10)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    void limitaASolicitacoesPendentesSimultaneas() {
        service.solicitar(form(ana, bruno, 10));
        service.solicitar(form(ana, bruno, 10));
        service.solicitar(form(ana, bruno, 10));

        assertThatThrownBy(() -> service.solicitar(form(ana, bruno, 10)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("aguardando aprovação");
    }

    @Test
    void limitaOTotalTransferidoNoMes() {
        for (int i = 0; i < 3; i++) {
            TransferenciaMoedas t = service.solicitar(form(ana, bruno, 100));
            service.aprovar(t.getId(), professor.getId());
        }
        assertThat(service.disponivelNoMes(ana)).isZero();

        assertThatThrownBy(() -> service.solicitar(form(ana, bruno, 1)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Limite mensal");
    }

    @Test
    void transferenciaRejeitadaNaoConsomeOLimiteMensal() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 100));
        service.rejeitar(t.getId(), professor.getId(), "Transferência sem justificativa acadêmica.");

        assertThat(service.disponivelNoMes(ana)).isEqualTo(300);
    }

    @Test
    void professorDeOutraInstituicaoNaoAvalia() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));

        assertThatThrownBy(() -> service.aprovar(t.getId(), professorDeOutraInstituicao.getId()))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("professorId");
        assertThat(t.isPendente()).isTrue();
    }

    @Test
    void transferenciaEncerradaNaoPodeSerDecididaDeNovo() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));
        service.aprovar(t.getId(), professor.getId());

        assertThatThrownBy(() -> service.rejeitar(t.getId(), professor.getId(), "Mudei de ideia sobre isso."))
                .isInstanceOf(RegraNegocioException.class);
        assertThat(bruno.getSaldoMoedas()).isEqualTo(50);
    }

    @Test
    void pendenteAlemDoPrazoExpiraEDevolveOValor() {
        TransferenciaMoedas t = service.solicitar(form(ana, bruno, 50));

        assertThat(service.expirarVencidas(LocalDateTime.now().plusDays(6))).isZero();
        assertThat(service.expirarVencidas(LocalDateTime.now().plusDays(8))).isEqualTo(1);

        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.EXPIRADA);
        assertThat(ana.getSaldoMoedas()).isEqualTo(500);
    }

    @Test
    void transferenciaEntraNoExtratoEImpedeExclusaoDosAlunos() {
        service.solicitar(form(ana, bruno, 50));

        assertThat(transacaoRepository.extratoDoAluno(ana)).hasSize(1);
        assertThat(transacaoRepository.extratoDoAluno(bruno)).hasSize(1);
        assertThatThrownBy(() -> alunoService.excluir(bruno.getId()))
                .isInstanceOf(RegraNegocioException.class);
    }

    private static TransferenciaForm form(Aluno de, Aluno para, int valor) {
        TransferenciaForm f = new TransferenciaForm();
        f.setRemetenteId(de.getId());
        f.setDestinatarioId(para.getId());
        f.setValor(valor);
        f.setMotivo("Ajuda no trabalho em grupo");
        return f;
    }
}
