package br.pucminas.moedaestudantil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.EmpresaParceira;
import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.model.Transacao;

/**
 * Acesso às transações (envios, resgates e créditos semestrais), base do extrato (RN09).
 * As consultas JPQL usam TREAT para filtrar pelas subclasses da hierarquia JOINED.
 */
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("""
           select t from Transacao t
           where treat(t as EnvioMoedas).aluno = :aluno
              or treat(t as ResgateVantagem).aluno = :aluno
           order by t.dataHora desc
           """)
    List<Transacao> extratoDoAluno(@Param("aluno") Aluno aluno);

    @Query("""
           select t from Transacao t
           where treat(t as EnvioMoedas).professor = :professor
              or treat(t as CreditoSemestral).professor = :professor
           order by t.dataHora desc
           """)
    List<Transacao> extratoDoProfessor(@Param("professor") Professor professor);

    @Query("""
           select count(t) > 0 from Transacao t
           where treat(t as EnvioMoedas).aluno = :aluno
              or treat(t as ResgateVantagem).aluno = :aluno
           """)
    boolean alunoPossuiTransacoes(@Param("aluno") Aluno aluno);

    @Query("""
           select count(r) > 0 from ResgateVantagem r
           where r.vantagem.empresa = :empresa
           """)
    boolean empresaPossuiResgates(@Param("empresa") EmpresaParceira empresa);

    @Query("""
           select count(c) > 0 from CreditoSemestral c
           where c.professor = :professor and c.semestre = :semestre
           """)
    boolean professorJaCreditadoNoSemestre(@Param("professor") Professor professor,
                                           @Param("semestre") String semestre);
}
