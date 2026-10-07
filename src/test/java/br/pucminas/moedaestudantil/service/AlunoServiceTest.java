package br.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.controller.form.AlunoForm;
import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.EnvioMoedas;
import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;

@SpringBootTest
@Transactional
class AlunoServiceTest {

    @Autowired AlunoService alunoService;
    @Autowired InstituicaoEnsinoService instituicaoService;
    @Autowired ProfessorRepository professorRepository;
    @Autowired TransacaoRepository transacaoRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Long instituicaoId;

    @BeforeEach
    void setUp() {
        instituicaoId = instituicaoService.listar().get(0).getId();
    }

    @Test
    void cadastraAlunoComSaldoZeroESenhaCriptografada() {
        Aluno aluno = alunoService.cadastrar(
                DadosDeTeste.aluno("Maria", "Maria@Teste.com", DadosDeTeste.cpf("987654321"), instituicaoId));

        assertThat(aluno.getId()).isNotNull();
        assertThat(aluno.getEmail()).isEqualTo("maria@teste.com");
        assertThat(aluno.getCpf()).matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        assertThat(aluno.getEndereco().getUf()).isEqualTo("MG");
        assertThat(aluno.getSaldoMoedas()).isZero();
        assertThat(aluno.getSenhaHash()).isNotEqualTo("segredo1");
        assertThat(passwordEncoder.matches("segredo1", aluno.getSenhaHash())).isTrue();
    }

    @Test
    void naoPermiteCpfRepetidoMesmoComFormatacaoDiferente() {
        String cpf = DadosDeTeste.cpf("987654322");
        alunoService.cadastrar(DadosDeTeste.aluno("A", "a@teste.com", cpf, instituicaoId));

        AlunoForm repetido = DadosDeTeste.aluno("B", "b@teste.com", Documentos.apenasDigitos(cpf), instituicaoId);
        assertThatThrownBy(() -> alunoService.cadastrar(repetido))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("cpf");
    }

    @Test
    void naoPermiteEmailJaUsadoPorOutroTipoDeUsuario() {
        AlunoForm form = DadosDeTeste.aluno("C", "GLENDER@pucminas.br", DadosDeTeste.cpf("987654323"), instituicaoId);

        assertThatThrownBy(() -> alunoService.cadastrar(form))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("email");
    }

    @Test
    void exigeSenhaComTamanhoMinimoNoCadastro() {
        AlunoForm form = DadosDeTeste.aluno("D", "d@teste.com", DadosDeTeste.cpf("987654324"), instituicaoId);
        form.setSenha("123");

        assertThatThrownBy(() -> alunoService.cadastrar(form))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("senha");
    }

    @Test
    void atualizaDadosEMantemSenhaQuandoEmBranco() {
        Aluno aluno = alunoService.cadastrar(
                DadosDeTeste.aluno("E", "e@teste.com", DadosDeTeste.cpf("987654325"), instituicaoId));
        String hashAnterior = aluno.getSenhaHash();

        AlunoForm edicao = AlunoForm.de(aluno);
        edicao.setNome("E Atualizado");
        edicao.setCurso("Ciência da Computação");
        edicao.setSenha("");
        Aluno atualizado = alunoService.atualizar(aluno.getId(), edicao);

        assertThat(atualizado.getNome()).isEqualTo("E Atualizado");
        assertThat(atualizado.getCurso()).isEqualTo("Ciência da Computação");
        assertThat(atualizado.getSenhaHash()).isEqualTo(hashAnterior);
    }

    @Test
    void excluiAlunoSemTransacoes() {
        Aluno aluno = alunoService.cadastrar(
                DadosDeTeste.aluno("F", "f@teste.com", DadosDeTeste.cpf("987654326"), instituicaoId));

        alunoService.excluir(aluno.getId());

        assertThatThrownBy(() -> alunoService.buscar(aluno.getId()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void naoExcluiAlunoQueJaRecebeuMoedas() {
        Aluno aluno = alunoService.cadastrar(
                DadosDeTeste.aluno("G", "g@teste.com", DadosDeTeste.cpf("987654327"), instituicaoId));
        Professor professor = professorRepository.findAll().get(0);
        professor.debitarEnvio(10);
        aluno.receberMoedas(10);
        transacaoRepository.save(new EnvioMoedas(professor, aluno, 10, "Participação em aula"));

        assertThatThrownBy(() -> alunoService.excluir(aluno.getId()))
                .isInstanceOf(RegraNegocioException.class);
        assertThat(transacaoRepository.extratoDoAluno(aluno)).hasSize(1);
    }
}
