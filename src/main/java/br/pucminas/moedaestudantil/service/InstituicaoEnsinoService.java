package br.pucminas.moedaestudantil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;

@Service
@Transactional(readOnly = true)
public class InstituicaoEnsinoService {

    private final InstituicaoEnsinoRepository repository;

    public InstituicaoEnsinoService(InstituicaoEnsinoRepository repository) {
        this.repository = repository;
    }

    public List<InstituicaoEnsino> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    public InstituicaoEnsino buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("instituicaoId", "Instituição de ensino inexistente."));
    }
}
