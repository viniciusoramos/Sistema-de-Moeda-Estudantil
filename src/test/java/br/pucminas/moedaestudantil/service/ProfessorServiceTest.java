package br.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.model.SaldoInsuficienteException;
import br.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;

@SpringBootTest
@Transactional
class ProfessorServiceTest {

    @Autowired ProfessorService professorService;
    @Autowired ProfessorRepository professorRepository;
    @Autowired TransacaoRepository transacaoRepository;

    @Test
    void cargaInicialCreditaMilMoedasACadaProfessor() {
        String semestreAtual = ProfessorService.semestreDe(LocalDate.now());
        assertThat(professorRepository.findAll())
                .isNotEmpty()
                .allSatisfy(p -> assertThat(transacaoRepository.professorJaCreditadoNoSemestre(p, semestreAtual)).isTrue());
    }

    @Test
    void creditoSemestralAcumulaENaoRepeteOMesmoSemestre() {
        Professor professor = professorRepository.findAll().get(0);
        int saldoInicial = professor.getSaldoMoedas();
        int transacoesAntes = transacaoRepository.extratoDoProfessor(professor).size();

        int primeira = professorService.creditarSemestre("2030/1");
        int repetida = professorService.creditarSemestre("2030/1");

        assertThat(primeira).isEqualTo(professorRepository.count());
        assertThat(repetida).isZero();
        assertThat(professor.getSaldoMoedas()).isEqualTo(saldoInicial + 1000);
        assertThat(transacaoRepository.extratoDoProfessor(professor)).hasSize(transacoesAntes + 1);
    }

    @Test
    void professorNaoEnviaMaisDoQueOSaldo() {
        Professor professor = professorRepository.findAll().get(0);

        assertThatThrownBy(() -> professor.debitarEnvio(professor.getSaldoMoedas() + 1))
                .isInstanceOf(SaldoInsuficienteException.class);
    }

    @Test
    void calculaSemestrePelaData() {
        assertThat(ProfessorService.semestreDe(LocalDate.of(2026, 3, 10))).isEqualTo("2026/1");
        assertThat(ProfessorService.semestreDe(LocalDate.of(2026, 10, 7))).isEqualTo("2026/2");
    }
}
