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

import br.pucminas.moedaestudantil.controller.form.EmpresaParceiraForm;
import br.pucminas.moedaestudantil.model.EmpresaParceira;
import br.pucminas.moedaestudantil.service.EmpresaParceiraService;
import br.pucminas.moedaestudantil.service.RegraNegocioException;

/** CRUD de empresa parceira: cadastro (UC04) e manutenção do cadastro (UC05). */
@Controller
@RequestMapping("/empresas")
public class EmpresaParceiraController {

    private final EmpresaParceiraService empresaService;

    public EmpresaParceiraController(EmpresaParceiraService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empresas", empresaService.listar());
        return "empresas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(model, new EmpresaParceiraForm());
    }

    @PostMapping
    public String cadastrar(@Validated @ModelAttribute("form") EmpresaParceiraForm form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(model, form);
        }
        try {
            EmpresaParceira empresa = empresaService.cadastrar(form);
            redirect.addFlashAttribute("sucesso", "Empresa parceira cadastrada com sucesso.");
            return "redirect:/empresas/" + empresa.getId();
        } catch (RegraNegocioException e) {
            Erros.registrar(result, e);
            return formulario(model, form);
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        model.addAttribute("empresa", empresaService.buscarComVantagens(id));
        return "empresas/detalhe";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(model, EmpresaParceiraForm.de(empresaService.buscar(id)));
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Validated @ModelAttribute("form") EmpresaParceiraForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        form.setId(id);
        if (result.hasErrors()) {
            return formulario(model, form);
        }
        try {
            empresaService.atualizar(id, form);
            redirect.addFlashAttribute("sucesso", "Cadastro atualizado.");
            return "redirect:/empresas/" + id;
        } catch (RegraNegocioException e) {
            Erros.registrar(result, e);
            return formulario(model, form);
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            empresaService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Empresa parceira excluída.");
            return "redirect:/empresas";
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/empresas/" + id;
        }
    }

    private String formulario(Model model, EmpresaParceiraForm form) {
        model.addAttribute("form", form);
        return "empresas/formulario";
    }
}
