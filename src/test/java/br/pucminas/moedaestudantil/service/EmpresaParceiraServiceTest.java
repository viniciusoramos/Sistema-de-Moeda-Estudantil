package br.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.moedaestudantil.controller.form.EmpresaParceiraForm;
import br.pucminas.moedaestudantil.model.EmpresaParceira;
import br.pucminas.moedaestudantil.model.Vantagem;
import br.pucminas.moedaestudantil.repository.VantagemRepository;

@SpringBootTest
@Transactional
class EmpresaParceiraServiceTest {

    @Autowired EmpresaParceiraService empresaService;
    @Autowired VantagemRepository vantagemRepository;

    @Test
    void cadastraEmpresaComCnpjFormatado() {
        String cnpj = DadosDeTeste.cnpj("123456780001");
        EmpresaParceira empresa = empresaService.cadastrar(
                DadosDeTeste.empresa("Livraria X", "livraria@teste.com", Documentos.apenasDigitos(cnpj)));

        assertThat(empresa.getId()).isNotNull();
        assertThat(empresa.getCnpj()).isEqualTo(cnpj);
    }

    @Test
    void naoPermiteCnpjRepetido() {
        String cnpj = DadosDeTeste.cnpj("123456780002");
        empresaService.cadastrar(DadosDeTeste.empresa("A", "a@empresa.com", cnpj));

        assertThatThrownBy(() -> empresaService.cadastrar(DadosDeTeste.empresa("B", "b@empresa.com", cnpj)))
                .isInstanceOf(RegraNegocioException.class)
                .extracting("campo").isEqualTo("cnpj");
    }

    @Test
    void atualizaTelefoneVazioComoNulo() {
        EmpresaParceira empresa = empresaService.cadastrar(
                DadosDeTeste.empresa("C", "c@empresa.com", DadosDeTeste.cnpj("123456780003")));

        EmpresaParceiraForm edicao = EmpresaParceiraForm.de(empresa);
        edicao.setTelefone("  ");
        edicao.setNome("C Ltda");

        EmpresaParceira atualizada = empresaService.atualizar(empresa.getId(), edicao);
        assertThat(atualizada.getNome()).isEqualTo("C Ltda");
        assertThat(atualizada.getTelefone()).isNull();
    }

    @Test
    void excluirEmpresaRemoveSuasVantagens() {
        EmpresaParceira empresa = empresaService.cadastrar(
                DadosDeTeste.empresa("D", "d@empresa.com", DadosDeTeste.cnpj("123456780004")));
        empresa.adicionarVantagem(new Vantagem("Brinde", "Um brinde", "http://foto", 10));
        long vantagensAntes = vantagemRepository.count();

        empresaService.excluir(empresa.getId());
        vantagemRepository.flush();

        assertThat(vantagemRepository.count()).isEqualTo(vantagensAntes - 1);
    }
}
