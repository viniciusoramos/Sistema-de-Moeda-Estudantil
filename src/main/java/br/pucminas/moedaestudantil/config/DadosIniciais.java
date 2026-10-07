package br.pucminas.moedaestudantil.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.EmpresaParceira;
import br.pucminas.moedaestudantil.model.Endereco;
import br.pucminas.moedaestudantil.model.EnvioMoedas;
import br.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.pucminas.moedaestudantil.model.Professor;
import br.pucminas.moedaestudantil.model.Vantagem;
import br.pucminas.moedaestudantil.repository.AlunoRepository;
import br.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.pucminas.moedaestudantil.service.ProfessorService;

/**
 * Carga inicial: instituições e professores são pré-cadastrados (RN01, RN02), pois chegam
 * pela parceria com a instituição e não têm tela de cadastro. Também cria uma empresa e dois
 * alunos de demonstração, um deles já com moedas recebidas de um professor. Só roda com o banco vazio. Senha de todos os usuários: 123456.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);
    private static final String SENHA_PADRAO = "123456";

    private final InstituicaoEnsinoRepository instituicaoRepository;
    private final ProfessorRepository professorRepository;
    private final EmpresaParceiraRepository empresaRepository;
    private final AlunoRepository alunoRepository;
    private final TransacaoRepository transacaoRepository;
    private final ProfessorService professorService;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(InstituicaoEnsinoRepository instituicaoRepository, ProfessorRepository professorRepository,
                         EmpresaParceiraRepository empresaRepository, AlunoRepository alunoRepository,
                         TransacaoRepository transacaoRepository, ProfessorService professorService,
                         PasswordEncoder passwordEncoder) {
        this.instituicaoRepository = instituicaoRepository;
        this.professorRepository = professorRepository;
        this.empresaRepository = empresaRepository;
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
        this.professorService = professorService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (instituicaoRepository.count() > 0) {
            return;
        }
        String senha = passwordEncoder.encode(SENHA_PADRAO);

        InstituicaoEnsino puc = instituicaoRepository.save(
                new InstituicaoEnsino("Pontifícia Universidade Católica de Minas Gerais", "PUC Minas"));
        InstituicaoEnsino ufmg = instituicaoRepository.save(
                new InstituicaoEnsino("Universidade Federal de Minas Gerais", "UFMG"));
        InstituicaoEnsino cefet = instituicaoRepository.save(
                new InstituicaoEnsino("Centro Federal de Educação Tecnológica de Minas Gerais", "CEFET-MG"));

        Professor glender = professorRepository.save(new Professor("Glender Brás", "glender@pucminas.br", senha,
                "529.982.247-25", "Engenharia de Software", puc));
        professorRepository.save(new Professor("Ana Paula Souza", "ana.souza@pucminas.br", senha,
                "168.995.350-09", "Ciência da Computação", puc));
        professorRepository.save(new Professor("Carlos Mendes", "carlos.mendes@ufmg.br", senha,
                "111.444.777-35", "Departamento de Matemática", ufmg));
        professorRepository.save(new Professor("Beatriz Lima", "beatriz.lima@cefetmg.br", senha,
                "390.533.447-05", "Departamento de Computação", cefet));

        int creditados = professorService.creditarSemestre(ProfessorService.semestreDe(LocalDate.now()));

        EmpresaParceira cantina = new EmpresaParceira("Cantina Coração Eucarístico", "contato@cantinace.com.br",
                senha, "11.222.333/0001-81", "(31) 3333-4444");
        cantina.adicionarVantagem(new Vantagem("Almoço com 20% de desconto",
                "Desconto de 20% em qualquer prato executivo do restaurante universitário.",
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600", 150));
        cantina.adicionarVantagem(new Vantagem("Café + pão de queijo",
                "Um café expresso e um pão de queijo grátis.",
                "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600", 40));
        empresaRepository.save(cantina);

        Aluno vinicius = alunoRepository.save(new Aluno("Vinícius Ramos", "vinicius@aluno.pucminas.br", senha,
                "123.456.789-09", "MG-12.345.678",
                new Endereco("Rua Dom José Gaspar", "500", "Prédio 34", "Coração Eucarístico",
                        "Belo Horizonte", "MG", "30535-901"),
                "Engenharia de Software", puc));
        alunoRepository.save(new Aluno("Larissa Costa", "larissa@aluno.pucminas.br", senha,
                "456.789.123-69", "MG-98.765.432",
                new Endereco("Avenida Brasil", "1200", null, "Funcionários",
                        "Belo Horizonte", "MG", "30140-002"),
                "Engenharia de Software", puc));

        // Primeiro reconhecimento, para o aluno de demonstração ter saldo para transferir
        glender.debitarEnvio(200);
        vinicius.receberMoedas(200);
        transacaoRepository.save(new EnvioMoedas(glender, vinicius, 200,
                "Excelente participação nas aulas de Laboratório de Desenvolvimento de Software."));

        log.info("Carga inicial concluída: 3 instituições, 4 professores ({} creditados), 1 empresa, 2 alunos.",
                creditados);
    }
}
