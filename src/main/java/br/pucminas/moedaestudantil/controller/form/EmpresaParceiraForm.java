package br.pucminas.moedaestudantil.controller.form;

import org.hibernate.validator.constraints.br.CNPJ;

import br.pucminas.moedaestudantil.model.EmpresaParceira;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados do formulário de cadastro/edição de empresa parceira (View → Controller). */
public class EmpresaParceiraForm {

    private Long id;

    @NotBlank(message = "Informe o nome da empresa.")
    @Size(max = 120)
    private String nome;

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail inválido.")
    @Size(max = 120)
    private String email;

    /** Obrigatória no cadastro; na edição, em branco mantém a senha atual. */
    private String senha;

    @NotBlank(message = "Informe o CNPJ.")
    @CNPJ(message = "CNPJ inválido.")
    private String cnpj;

    @Size(max = 20)
    private String telefone;

    public static EmpresaParceiraForm de(EmpresaParceira empresa) {
        EmpresaParceiraForm f = new EmpresaParceiraForm();
        f.id = empresa.getId();
        f.nome = empresa.getNome();
        f.email = empresa.getEmail();
        f.cnpj = empresa.getCnpj();
        f.telefone = empresa.getTelefone();
        return f;
    }

    public boolean isNovo() { return id == null; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
