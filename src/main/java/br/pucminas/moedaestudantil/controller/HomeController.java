package br.pucminas.moedaestudantil.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.pucminas.moedaestudantil.service.AlunoService;
import br.pucminas.moedaestudantil.service.EmpresaParceiraService;
import br.pucminas.moedaestudantil.service.InstituicaoEnsinoService;
import br.pucminas.moedaestudantil.service.ProfessorService;

@Controller
public class HomeController {

    private final AlunoService alunoService;
    private final EmpresaParceiraService empresaService;
    private final ProfessorService professorService;
    private final InstituicaoEnsinoService instituicaoService;

    public HomeController(AlunoService alunoService, EmpresaParceiraService empresaService,
                          ProfessorService professorService, InstituicaoEnsinoService instituicaoService) {
        this.alunoService = alunoService;
        this.empresaService = empresaService;
        this.professorService = professorService;
        this.instituicaoService = instituicaoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalAlunos", alunoService.listar().size());
        model.addAttribute("totalEmpresas", empresaService.listar().size());
        model.addAttribute("professores", professorService.listar());
        model.addAttribute("instituicoes", instituicaoService.listar());
        return "index";
    }
}
