package com.tassisfernando.chamegoapi.application.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import com.tassisfernando.chamegoapi.domain.producao.LoteProducao;
import com.tassisfernando.chamegoapi.domain.producao.ProducaoRepository;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComReceita;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarProducaoUseCaseTest {

    @Mock
    private ProducaoRepository producaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private RegistrarProducaoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarProducaoUseCase(
                producaoRepository, produtoRepository, insumoRepository, movimentoEstoqueRepository);
        lenient()
                .when(producaoRepository.salvar(any(LoteProducao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveConsumirReceitaEEntrarEstoqueDoProduto() {
        Insumo cera = ceraComEstoque("1000", "4.20");
        Produto produto = velaComReceita(cera, quantidade("100", UnidadeMedida.G));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(insumoRepository.buscarPorId(cera.id())).thenReturn(Optional.of(cera));

        LoteProducao lote = useCase.executar(new RegistrarProducaoComando(
                produto.id(), LocalDate.of(2025, 12, 16), 2, null));

        assertEquals(2, lote.quantidadeProduzida());
        assertEquals(2, produto.estoqueAtual());
        assertEquals(quantidade("800", UnidadeMedida.G), cera.estoqueAtual());
        verify(insumoRepository).salvar(cera);
        verify(produtoRepository).salvar(produto);
        verify(movimentoEstoqueRepository, times(2)).salvar(any());
    }

    @Test
    void deveRecusarProducaoSemReceitaCadastrada() {
        Produto produto = velaComEstoque(0);
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));

        assertThrows(
                RegraDeNegocioViolada.class,
                () -> useCase.executar(new RegistrarProducaoComando(produto.id(), LocalDate.now(), 1, null)));
    }
}
