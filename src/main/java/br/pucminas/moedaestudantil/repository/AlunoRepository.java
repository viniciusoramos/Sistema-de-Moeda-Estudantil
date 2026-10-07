package br.pucminas.moedaestudantil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.moedaestudantil.model.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findAllByOrderByNomeAsc();

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);
}
