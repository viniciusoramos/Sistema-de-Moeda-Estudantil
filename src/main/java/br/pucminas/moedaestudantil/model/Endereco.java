package br.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Endereço do aluno; persistido nas colunas da própria tabela aluno. */
@Embeddable
public class Endereco {

    @Column(length = 120)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 60)
    private String complemento;

    @Column(length = 60)
    private String bairro;

    @Column(length = 60)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 9)
    private String cep;

    public Endereco() {
    }

    public Endereco(String logradouro, String numero, String complemento,
                    String bairro, String cidade, String uf, String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    @Override
    public String toString() {
        String comp = complemento == null || complemento.isBlank() ? "" : " - " + complemento;
        return logradouro + ", " + numero + comp + " — " + bairro + ", " + cidade + "/" + uf + " — CEP " + cep;
    }
}
