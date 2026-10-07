package br.pucminas.moedaestudantil.model;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(int saldo, int valor) {
        super("Saldo insuficiente: saldo atual de " + saldo + " moeda(s), operação de " + valor + " moeda(s).");
    }
}
