package br.pucminas.moedaestudantil.controller.form;

import org.hibernate.validator.constraints.br.CPF;

import br.pucminas.moedaestudantil.model.Aluno;
import br.pucminas.moedaestudantil.model.Endereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados do formulário de cadastro/edição de aluno (View → Controller). */
public class AlunoForm {

    private Long id;

    @NotBlank(message = "Informe o nome.")
    @Size(max = 120)
    private String nome;

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail inválido.")
    @Size(max = 120)
    private String email;

    /** Obrigatória no cadastro; na edição, em branco mantém a senha atual. */
    private String senha;

    @NotBlank(message = "Informe o CPF.")
    @CPF(message = "CPF inválido.")
    private String cpf;

    @NotBlank(message = "Informe o RG.")
    @Size(max = 20)
    private String rg;

    @NotBlank(message = "Informe o logradouro.")
    @Size(max = 120)
    private String logradouro;

    @NotBlank(message = "Informe o número.")
    @Size(max = 10)
    private String numero;

    @Size(max = 60)
    private String complemento;

    @NotBlank(message = "Informe o bairro.")
    @Size(max = 60)
    private String bairro;

    @NotBlank(message = "Informe a cidade.")
    @Size(max = 60)
    private String cidade;

    @NotBlank(message = "Informe a UF.")
    @Pattern(regexp = "[A-Za-z]{2}", message = "UF deve ter 2 letras.")
    private String uf;

    @NotBlank(message = "Informe o CEP.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido (formato 00000-000).")
    private String cep;

    @NotBlank(message = "Informe o curso.")
    @Size(max = 80)
    private String curso;

    @NotNull(message = "Selecione a instituição de ensino.")
    private Long instituicaoId;

    public static AlunoForm de(Aluno aluno) {
        AlunoForm f = new AlunoForm();
        f.id = aluno.getId();
        f.nome = aluno.getNome();
        f.email = aluno.getEmail();
        f.cpf = aluno.getCpf();
        f.rg = aluno.getRg();
        Endereco e = aluno.getEndereco();
        if (e != null) {
            f.logradouro = e.getLogradouro();
            f.numero = e.getNumero();
            f.complemento = e.getComplemento();
            f.bairro = e.getBairro();
            f.cidade = e.getCidade();
            f.uf = e.getUf();
            f.cep = e.getCep();
        }
        f.curso = aluno.getCurso();
        f.instituicaoId = aluno.getInstituicao().getId();
        return f;
    }

    public Endereco toEndereco() {
        return new Endereco(logradouro.trim(), numero.trim(),
                complemento == null ? null : complemento.trim(),
                bairro.trim(), cidade.trim(), uf.trim().toUpperCase(), cep.trim());
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
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
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
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public Long getInstituicaoId() { return instituicaoId; }
    public void setInstituicaoId(Long instituicaoId) { this.instituicaoId = instituicaoId; }
}
