package br.pucminas.moedaestudantil.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.StatusTransferencia;
import br.pucminas.moedaestudantil.model.TransferenciaMoedas;

public interface TransferenciaMoedasRepository extends JpaRepository<TransferenciaMoedas, Long> {

    List<TransferenciaMoedas> findAllByOrderByDataHoraDesc();

    List<TransferenciaMoedas> findByStatusOrderByDataHoraAsc(StatusTransferencia status);

    List<TransferenciaMoedas> findByStatusAndDataHoraBefore(StatusTransferencia status, LocalDateTime limite);

    long countByRemetenteAndStatus(Aluno remetente, StatusTransferencia status);

    @Query("""
           select t from TransferenciaMoedas t
           where t.remetente = :aluno or t.destinatario = :aluno
           order by t.dataHora desc
           """)
    List<TransferenciaMoedas> doAluno(@Param("aluno") Aluno aluno);

    /** Soma do que o aluno já comprometeu desde {@code inicio}, para o limite mensal (RN15). */
    @Query("""
           select coalesce(sum(t.valor), 0) from TransferenciaMoedas t
           where t.remetente = :remetente
             and t.status in :status
             and t.dataHora >= :inicio
           """)
    int somaEnviadaDesde(@Param("remetente") Aluno remetente,
                         @Param("status") Collection<StatusTransferencia> status,
                         @Param("inicio") LocalDateTime inicio);
}
