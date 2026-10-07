package br.pucminas.moedaestudantil.service;

import br.pucminas.moedaestudantil.controller.form.AlunoForm;
import br.pucminas.moedaestudantil.controller.form.EmpresaParceiraForm;

/** Gera documentos válidos (com dígitos verificadores corretos) e formulários prontos para os testes. */
final class DadosDeTeste {

    private DadosDeTeste() {
    }

    /** CPF válido a partir dos 9 primeiros dígitos. */
    static String cpf(String noveDigitos) {
        String base = noveDigitos;
        base += digito(base, 10);
        base += digito(base, 11);
        return Documentos.formatarCpf(base);
    }

    /** CNPJ válido a partir dos 12 primeiros dígitos. */
    static String cnpj(String dozeDigitos) {
        int[] p1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] p2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        String base = dozeDigitos;
        base += digitoCnpj(base, p1);
        base += digitoCnpj(base, p2);
        return Documentos.formatarCnpj(base);
    }

    static AlunoForm aluno(String nome, String email, String cpf, Long instituicaoId) {
        AlunoForm f = new AlunoForm();
        f.setNome(nome);
        f.setEmail(email);
        f.setSenha("segredo1");
        f.setCpf(cpf);
        f.setRg("MG-1.234.567");
        f.setLogradouro("Rua A");
        f.setNumero("10");
        f.setBairro("Centro");
        f.setCidade("Belo Horizonte");
        f.setUf("mg");
        f.setCep("30000-000");
        f.setCurso("Engenharia de Software");
        f.setInstituicaoId(instituicaoId);
        return f;
    }

    static EmpresaParceiraForm empresa(String nome, String email, String cnpj) {
        EmpresaParceiraForm f = new EmpresaParceiraForm();
        f.setNome(nome);
        f.setEmail(email);
        f.setSenha("segredo1");
        f.setCnpj(cnpj);
        f.setTelefone("(31) 99999-0000");
        return f;
    }

    private static int digito(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static int digitoCnpj(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
