package br.pucminas.moedaestudantil.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.controller.form.EmpresaParceiraForm;
import br.pucminas.moedaestudantil.model.EmpresaParceira;
import br.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.pucminas.moedaestudantil.repository.UsuarioRepository;

/** Regras do CRUD de empresa parceira (UC04 e UC05). */
@Service
@Transactional
public class EmpresaParceiraService {

    private final EmpresaParceiraRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaParceiraService(EmpresaParceiraRepository empresaRepository, UsuarioRepository usuarioRepository,
                                  TransacaoRepository transacaoRepository, PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<EmpresaParceira> listar() {
        return empresaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public EmpresaParceira buscar(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa parceira", id));
    }

    /** Carrega a empresa já com as vantagens, para a tela de detalhe. */
    @Transactional(readOnly = true)
    public EmpresaParceira buscarComVantagens(Long id) {
        return empresaRepository.findComVantagensById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa parceira", id));
    }

    public EmpresaParceira cadastrar(EmpresaParceiraForm form) {
        String cnpj = Documentos.formatarCnpj(form.getCnpj());
        String email = form.getEmail().trim().toLowerCase();
        AlunoService.validarSenhaObrigatoria(form.getSenha());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("email", "Já existe um usuário com este e-mail.");
        }
        if (empresaRepository.existsByCnpj(cnpj)) {
            throw new RegraNegocioException("cnpj", "Já existe uma empresa com este CNPJ.");
        }
        EmpresaParceira empresa = new EmpresaParceira(form.getNome().trim(), email,
                passwordEncoder.encode(form.getSenha()), cnpj, vazioParaNulo(form.getTelefone()));
        return empresaRepository.save(empresa);
    }

    public EmpresaParceira atualizar(Long id, EmpresaParceiraForm form) {
        EmpresaParceira empresa = buscar(id);
        String cnpj = Documentos.formatarCnpj(form.getCnpj());
        String email = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RegraNegocioException("email", "Já existe um usuário com este e-mail.");
        }
        if (empresaRepository.existsByCnpjAndIdNot(cnpj, id)) {
            throw new RegraNegocioException("cnpj", "Já existe uma empresa com este CNPJ.");
        }
        if (form.getSenha() != null && !form.getSenha().isBlank()) {
            AlunoService.validarSenhaObrigatoria(form.getSenha());
            empresa.setSenhaHash(passwordEncoder.encode(form.getSenha()));
        }
        empresa.setNome(form.getNome().trim());
        empresa.setEmail(email);
        empresa.setCnpj(cnpj);
        empresa.setTelefone(vazioParaNulo(form.getTelefone()));
        return empresa;
    }

    /** Exclusão só é permitida se nenhuma vantagem da empresa já foi resgatada (RN12). */
    public void excluir(Long id) {
        EmpresaParceira empresa = buscar(id);
        if (transacaoRepository.empresaPossuiResgates(empresa)) {
            throw new RegraNegocioException(
                    "A empresa possui vantagens já resgatadas por alunos e não pode ser excluída.");
        }
        empresaRepository.delete(empresa);
    }

    private static String vazioParaNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
