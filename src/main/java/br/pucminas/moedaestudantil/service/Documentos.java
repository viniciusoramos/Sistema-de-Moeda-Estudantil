package br.pucminas.moedaestudantil.service;

/** Normaliza CPF e CNPJ para um formato único antes de gravar ou comparar. */
public final class Documentos {

    private Documentos() {
    }

    public static String apenasDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    /** 12345678909 → 123.456.789-09 */
    public static String formatarCpf(String cpf) {
        String d = apenasDigitos(cpf);
        if (d.length() != 11) {
            return cpf;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    /** 11222333000181 → 11.222.333/0001-81 */
    public static String formatarCnpj(String cnpj) {
        String d = apenasDigitos(cnpj);
        if (d.length() != 14) {
            return cnpj;
        }
        return d.substring(0, 2) + "." + d.substring(2, 5) + "." + d.substring(5, 8) + "/"
                + d.substring(8, 12) + "-" + d.substring(12);
    }
}
