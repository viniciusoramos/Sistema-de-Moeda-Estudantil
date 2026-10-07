package br.pucminas.moedaestudantil.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.pucminas.moedaestudantil.controller.form.TransferenciaForm;
import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.TransferenciaMoedas;
import br.pucminas.moedaestudantil.service.AlunoService;
import br.pucminas.moedaestudantil.service.RegraNegocioException;
import br.pucminas.moedaestudantil.service.TransferenciaService;

/**
 * Transferência de moedas entre alunos: solicitar (UC14), avaliar (UC15) e cancelar (UC16).
 * Enquanto o login não existe (Lab03S03), remetente e professor avaliador são escolhidos na tela.
 */
@Controller
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;
    private final AlunoService alunoService;

    public TransferenciaController(TransferenciaService transferenciaService, AlunoService alunoService) {
        this.transferenciaService = transferenciaService;
        this.alunoService = alunoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pendentes", transferenciaService.listarPendentes());
        model.addAttribute("transferencias", transferenciaService.listar());
        model.addAttribute("regras", transferenciaService.getRegras());
        return "transferencias/lista";
    }

    @GetMapping("/nova")
    public String nova(@RequestParam(required = false) Long remetenteId, Model model) {
        TransferenciaForm form = new TransferenciaForm();
        form.setRemetenteId(remetenteId);
        return formulario(model, form);
    }

    @PostMapping
    public String solicitar(@Validated @ModelAttribute("form") TransferenciaForm form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(model, form);
        }
        try {
            TransferenciaMoedas t = transferenciaService.solicitar(form);
            redirect.addFlashAttribute("sucesso",
                    "Transferência solicitada. O valor ficou reservado até a decisão de um professor.");
            return "redirect:/transferencias/" + t.getId();
        } catch (RegraNegocioException e) {
            Erros.registrar(result, e);
            return formulario(model, form);
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        TransferenciaMoedas transferencia = transferenciaService.buscar(id);
        model.addAttribute("t", transferencia);
        model.addAttribute("avaliadores", transferenciaService.avaliadoresPossiveis(transferencia));
        model.addAttribute("regras", transferenciaService.getRegras());
        return "transferencias/detalhe";
    }

    @PostMapping("/{id}/aprovar")
    public String aprovar(@PathVariable Long id, @RequestParam(required = false) Long professorId,
                          RedirectAttributes redirect) {
        return decidir(id, redirect, "Transferência aprovada e creditada ao destinatário.",
                () -> transferenciaService.aprovar(id, professorId));
    }

    @PostMapping("/{id}/rejeitar")
    public String rejeitar(@PathVariable Long id, @RequestParam(required = false) Long professorId,
                           @RequestParam(required = false) String justificativa, RedirectAttributes redirect) {
        return decidir(id, redirect, "Transferência rejeitada. O valor voltou ao remetente.",
                () -> transferenciaService.rejeitar(id, professorId, justificativa));
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, @RequestParam Long alunoId, RedirectAttributes redirect) {
        return decidir(id, redirect, "Transferência cancelada. O valor voltou ao remetente.",
                () -> transferenciaService.cancelar(id, alunoId));
    }

    private String decidir(Long id, RedirectAttributes redirect, String mensagem, Runnable acao) {
        try {
            acao.run();
            redirect.addFlashAttribute("sucesso", mensagem);
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/transferencias/" + id;
    }

    private String formulario(Model model, TransferenciaForm form) {
        List<Aluno> alunos = alunoService.listar();
        Aluno remetente = form.getRemetenteId() == null ? null
                : alunos.stream().filter(a -> a.getId().equals(form.getRemetenteId())).findFirst().orElse(null);
        List<Aluno> destinatarios = remetente == null ? alunos : alunos.stream()
                .filter(a -> !a.getId().equals(remetente.getId()))
                .filter(a -> Objects.equals(a.getInstituicao().getId(), remetente.getInstituicao().getId()))
                .toList();

        model.addAttribute("form", form);
        model.addAttribute("alunos", alunos);
        model.addAttribute("destinatarios", destinatarios);
        model.addAttribute("remetente", remetente);
        model.addAttribute("disponivelNoMes", remetente == null ? null : transferenciaService.disponivelNoMes(remetente));
        model.addAttribute("regras", transferenciaService.getRegras());
        return "transferencias/formulario";
    }
}
