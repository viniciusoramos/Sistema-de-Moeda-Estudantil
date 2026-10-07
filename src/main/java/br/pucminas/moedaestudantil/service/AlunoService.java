package br.pucminas.moedaestudantil.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.controller.form.AlunoForm;
import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.pucminas.moedaestudantil.repository.AlunoRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.pucminas.moedaestudantil.repository.UsuarioRepository;

/** Regras do CRUD de aluno (UC02 e UC03). */
@Service
@Transactional
public class AlunoService {

    static final int TAMANHO_MINIMO_SENHA = 6;

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final InstituicaoEnsinoService instituicaoService;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, UsuarioRepository usuarioRepository,
                        TransacaoRepository transacaoRepository, InstituicaoEnsinoService instituicaoService,
                        PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
        this.instituicaoService = instituicaoService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Aluno> listar() {
        return alunoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Aluno buscar(Long id) {
        return alunoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", id));
    }

    public Aluno cadastrar(AlunoForm form) {
        String cpf = Documentos.formatarCpf(form.getCpf());
        String email = form.getEmail().trim().toLowerCase();
        validarSenhaObrigatoria(form.getSenha());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("email", "Já existe um usuário com este e-mail.");
        }
        if (alunoRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("cpf", "Já existe um aluno com este CPF.");
        }
        InstituicaoEnsino instituicao = instituicaoService.buscar(form.getInstituicaoId());

        Aluno aluno = new Aluno(form.getNome().trim(), email, passwordEncoder.encode(form.getSenha()),
                cpf, form.getRg().trim(), form.toEndereco(), form.getCurso().trim(), instituicao);
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Long id, AlunoForm form) {
        Aluno aluno = buscar(id);
        String cpf = Documentos.formatarCpf(form.getCpf());
        String email = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RegraNegocioException("email", "Já existe um usuário com este e-mail.");
        }
        if (alunoRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new RegraNegocioException("cpf", "Já existe um aluno com este CPF.");
        }
        if (form.getSenha() != null && !form.getSenha().isBlank()) {
            validarSenhaObrigatoria(form.getSenha());
            aluno.setSenhaHash(passwordEncoder.encode(form.getSenha()));
        }
        aluno.setNome(form.getNome().trim());
        aluno.setEmail(email);
        aluno.setCpf(cpf);
        aluno.setRg(form.getRg().trim());
        aluno.setEndereco(form.toEndereco());
        aluno.setCurso(form.getCurso().trim());
        aluno.setInstituicao(instituicaoService.buscar(form.getInstituicaoId()));
        return aluno;
    }

    /** Exclusão só é permitida enquanto o aluno não tiver movimentações (RN12). */
    public void excluir(Long id) {
        Aluno aluno = buscar(id);
        if (transacaoRepository.alunoPossuiTransacoes(aluno)) {
            throw new RegraNegocioException(
                    "O aluno possui moedas recebidas ou trocadas e não pode ser excluído, para preservar o extrato.");
        }
        alunoRepository.delete(aluno);
    }

    static void validarSenhaObrigatoria(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException("senha",
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }
}
