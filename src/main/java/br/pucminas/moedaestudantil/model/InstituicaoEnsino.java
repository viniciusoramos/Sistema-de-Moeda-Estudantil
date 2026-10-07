package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Instituição participante; pré-cadastrada no sistema pela carga inicial (RN01). */
@Entity
@Table(name = "instituicao_ensino")
public class InstituicaoEnsino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(nullable = false, length = 20)
    private String sigla;

    protected InstituicaoEnsino() {
    }

    public InstituicaoEnsino(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getSigla() { return sigla; }
}
