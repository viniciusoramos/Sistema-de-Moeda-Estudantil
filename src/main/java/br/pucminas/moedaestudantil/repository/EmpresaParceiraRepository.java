package br.pucminas.moedaestudantil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.moedaestudantil.model.EmpresaParceira;

public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {

    List<EmpresaParceira> findAllByOrderByNomeAsc();

    @EntityGraph(attributePaths = "vantagens")
    Optional<EmpresaParceira> findComVantagensById(Long id);

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);
}
