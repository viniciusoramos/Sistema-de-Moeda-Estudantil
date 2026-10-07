package br.pucminas.moedaestudantil.controller;

import org.springframework.validation.BindingResult;

import br.pucminas.moedaestudantil.service.RegraNegocioException;

/** Leva o erro de regra de negócio para o campo correspondente do formulário. */
final class Erros {

    private Erros() {
    }

    static void registrar(BindingResult result, RegraNegocioException e) {
        if (e.getCampo() != null) {
            result.rejectValue(e.getCampo(), "regra", e.getMessage());
        } else {
            result.reject("regra", e.getMessage());
        }
    }
}
