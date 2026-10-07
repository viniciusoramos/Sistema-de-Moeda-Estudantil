package br.pucminas.moedaestudantil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.moedaestudantil.model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    List<Professor> findAllByOrderByNomeAsc();
}
