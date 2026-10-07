package br.pucminas.moedaestudantil.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/** Movimentação de moedas que compõe o extrato de alunos e professores (RN09). */
@Entity
@Table(name = "transacao")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private int valor;

    protected Transacao() {
    }

    protected Transacao(int valor) {
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public abstract String getDescricao();

    public Long getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public int getValor() { return valor; }
}
