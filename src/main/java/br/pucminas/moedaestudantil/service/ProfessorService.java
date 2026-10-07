package br.pucminas.moedaestudantil.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.model.CreditoSemestral;
import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;

@Service
@Transactional
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final TransacaoRepository transacaoRepository;

    public ProfessorService(ProfessorRepository professorRepository, TransacaoRepository transacaoRepository) {
        this.professorRepository = professorRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Professor> listar() {
        return professorRepository.findAllByOrderByNomeAsc();
    }

    /**
     * Credita as 1.000 moedas do semestre a cada professor que ainda não as recebeu (UC10, RN03).
     * O saldo anterior é mantido, e o mesmo semestre nunca é creditado duas vezes.
     *
     * @return quantidade de professores creditados
     */
    public int creditarSemestre(String semestre) {
        int creditados = 0;
        for (Professor professor : professorRepository.findAll()) {
            if (!transacaoRepository.professorJaCreditadoNoSemestre(professor, semestre)) {
                professor.receberCreditoSemestral();
                transacaoRepository.save(new CreditoSemestral(professor, semestre));
                creditados++;
            }
        }
        return creditados;
    }

    /** "2026/1" para janeiro–junho, "2026/2" para julho–dezembro. */
    public static String semestreDe(LocalDate data) {
        return data.getYear() + "/" + (data.getMonthValue() <= 6 ? 1 : 2);
    }
}
