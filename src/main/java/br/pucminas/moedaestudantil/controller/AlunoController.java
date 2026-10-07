package br.pucminas.moedaestudantil.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.pucminas.moedaestudantil.controller.form.AlunoForm;
import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.service.AlunoService;
import br.pucminas.moedaestudantil.service.InstituicaoEnsinoService;
import br.pucminas.moedaestudantil.service.RegraNegocioException;
import br.pucminas.moedaestudantil.service.TransferenciaService;

/** CRUD de aluno: cadastro (UC02) e manutenção do cadastro (UC03). */
@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final InstituicaoEnsinoService instituicaoService;
    private final TransferenciaService transferenciaService;

    public AlunoController(AlunoService alunoService, InstituicaoEnsinoService instituicaoService,
                           TransferenciaService transferenciaService) {
        this.alunoService = alunoService;
        this.instituicaoService = instituicaoService;
        this.transferenciaService = transferenciaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.listar());
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(model, new AlunoForm());
    }

    @PostMapping
    public String cadastrar(@Validated @ModelAttribute("form") AlunoForm form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(model, form);
        }
        try {
            Aluno aluno = alunoService.cadastrar(form);
            redirect.addFlashAttribute("sucesso", "Aluno cadastrado com sucesso.");
            return "redirect:/alunos/" + aluno.getId();
        } catch (RegraNegocioException e) {
            Erros.registrar(result, e);
            return formulario(model, form);
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.buscar(id);
        model.addAttribute("aluno", aluno);
        model.addAttribute("transferencias", transferenciaService.doAluno(aluno));
        return "alunos/detalhe";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(model, AlunoForm.de(alunoService.buscar(id)));
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Validated @ModelAttribute("form") AlunoForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        form.setId(id);
        if (result.hasErrors()) {
            return formulario(model, form);
        }
        try {
            alunoService.atualizar(id, form);
            redirect.addFlashAttribute("sucesso", "Cadastro atualizado.");
            return "redirect:/alunos/" + id;
        } catch (RegraNegocioException e) {
            Erros.registrar(result, e);
            return formulario(model, form);
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            alunoService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Aluno excluído.");
            return "redirect:/alunos";
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/alunos/" + id;
        }
    }

    private String formulario(Model model, AlunoForm form) {
        model.addAttribute("form", form);
        model.addAttribute("instituicoes", instituicaoService.listar());
        return "alunos/formulario";
    }
}
