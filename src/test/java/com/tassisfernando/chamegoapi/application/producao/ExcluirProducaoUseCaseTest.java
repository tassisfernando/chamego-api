package com.tassisfernando.chamegoapi.application.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import com.tassisfernando.chamegoapi.domain.producao.ItemConsumoProducao;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirProducaoUseCaseTest {

    @Mock
    private ProducaoRepository producaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private ExcluirProducaoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirProducaoUseCase(
                producaoRepository, produtoRepository, insumoRepository, movimentoEstoqueRepository);
    }

    @Test
    void reverteEstoqueDeProdutoERestauraInsumos() {
        UUID loteId = UUID.randomUUID();
        Produto produto = velaComEstoque(5);
        Insumo cera = ceraComEstoque("200", "0.05");
        ItemConsumoProducao consumo =
                new ItemConsumoProducao(cera.id(), quantidade("50", UnidadeMedida.G), brl("0.05"));
        LoteProducao lote = LoteProducao.reconstituir(
                loteId, produto.id(), LocalDate.now(), 2, List.of(consumo), null);

        when(producaoRepository.buscarPorId(loteId)).thenReturn(Optional.of(lote));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(insumoRepository.buscarPorId(cera.id())).thenReturn(Optional.of(cera));

        useCase.executar(loteId);

        verify(movimentoEstoqueRepository, atLeast(2)).salvar(any());
        verify(produtoRepository).salvar(produto);
        verify(insumoRepository).salvar(cera);
        verify(producaoRepository).excluir(loteId);
    }
}