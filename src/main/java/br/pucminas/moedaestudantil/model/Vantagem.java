package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Produto ou desconto oferecido por uma empresa parceira, com custo em moedas (RN07). */
@Entity
@Table(name = "vantagem")
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(name = "foto_url", nullable = false, length = 300)
    private String fotoUrl;

    @Column(name = "custo_moedas", nullable = false)
    private int custoMoedas;

    @Column(nullable = false)
    private boolean ativa = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaParceira empresa;

    protected Vantagem() {
    }

    public Vantagem(String titulo, String descricao, String fotoUrl, int custoMoedas) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.fotoUrl = fotoUrl;
        this.custoMoedas = custoMoedas;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public int getCustoMoedas() { return custoMoedas; }
    public void setCustoMoedas(int custoMoedas) { this.custoMoedas = custoMoedas; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public EmpresaParceira getEmpresa() { return empresa; }
    void setEmpresa(EmpresaParceira empresa) { this.empresa = empresa; }
}
