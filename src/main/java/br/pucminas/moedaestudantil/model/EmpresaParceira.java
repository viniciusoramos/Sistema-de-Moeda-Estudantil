package br.pucminas.moedaestudantil.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresa_parceira")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class EmpresaParceira extends Usuario {

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(length = 20)
    private String telefone;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vantagem> vantagens = new ArrayList<>();

    protected EmpresaParceira() {
    }

    public EmpresaParceira(String nome, String email, String senhaHash, String cnpj, String telefone) {
        super(nome, email, senhaHash);
        this.cnpj = cnpj;
        this.telefone = telefone;
    }

    public void adicionarVantagem(Vantagem vantagem) {
        vantagem.setEmpresa(this);
        vantagens.add(vantagem);
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.EMPRESA_PARCEIRA; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public List<Vantagem> getVantagens() { return vantagens; }
}
