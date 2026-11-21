package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import com.tassisfernando.chamegoapi.domain.precificacao.ItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrecificacaoMontagemServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private InsumoRepository insumoRepository;

    private PrecificacaoMontagemService service;

    @BeforeEach
    void setUp() {
        service = new PrecificacaoMontagemService(produtoRepository, insumoRepository);
    }

    @Test
    void montaInsumoComCustoMedioQuandoNaoInformado() {
        var cera = ceraComEstoque("1000", "0.05");
        when(insumoRepository.buscarPorId(cera.id())).thenReturn(Optional.of(cera));

        var comando = new LinhaPrecificacaoComando(
                TipoItemPrecificacao.INSUMO,
                null,
                cera.id(),
                null,
                new BigDecimal("200"),
                UnidadeMedida.G,
                null);

        ItemPrecificacao item = service.montarItens(List.of(comando)).getFirst();

        assertEquals(0, new BigDecimal("10.00").compareTo(item.subtotal().valor()));
    }

    @Test
    void somaCustoKitComDoisProdutosEExtra() {
        var vela = velaComEstoque(10);
        when(produtoRepository.buscarPorId(vela.id())).thenReturn(Optional.of(vela));

        var comandos = List.of(
                new LinhaPrecificacaoComando(
                        TipoItemPrecificacao.PRODUTO,
                        vela.id(),
                        null,
                        null,
                        new BigDecimal("2"),
                        null,
                        null),
                new LinhaPrecificacaoComando(
                        TipoItemPrecificacao.EXTRA,
                        null,
                        null,
                        "Caixa presente",
                        new BigDecimal("1"),
                        null,
                        new BigDecimal("5.50")));

        List<ItemPrecificacao> itens = service.montarItens(comandos);
        Dinheiro total = service.custoTotal(itens);

        assertEquals(0, new BigDecimal("38.40").compareTo(total.valor()));
    }

    @Test
    void itensDaFichaTecnicaUsaCustoMedioDoInsumo() {
        var cera = ceraComEstoque("500", "0.10");
        var vela = com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComReceita(
                cera, quantidade("150", UnidadeMedida.G));
        when(produtoRepository.buscarPorId(vela.id())).thenReturn(Optional.of(vela));
        when(insumoRepository.buscarPorId(cera.id())).thenReturn(Optional.of(cera));

        List<ItemPrecificacao> itens = service.itensDaFichaTecnica(vela.id());
        Dinheiro total = service.custoTotal(itens);

        assertEquals(0, new BigDecimal("15.00").compareTo(total.valor()));
    }
}