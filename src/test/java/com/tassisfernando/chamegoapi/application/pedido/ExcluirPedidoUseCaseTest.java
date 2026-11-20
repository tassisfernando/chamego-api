package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
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
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private ExcluirPedidoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirPedidoUseCase(
                pedidoRepository, new PedidoEstoqueService(produtoRepository, movimentoEstoqueRepository));
    }

    @Test
    void deveExcluirOrcamentoSemRestaurarEstoque() {
        UUID pedidoId = UUID.randomUUID();
        Pedido orcamento = Pedido.reconstituir(
                pedidoId,
                UUID.randomUUID(),
                LocalDate.now(),
                StatusPedido.ORCAMENTO,
                NaturezaPedido.VENDA,
                null,
                null,
                List.of(new ItemPedido(UUID.randomUUID(), 1, brl("10"), brl("5"))));
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(orcamento));

        useCase.executar(pedidoId);

        verify(pedidoRepository).excluir(pedidoId);
        verify(movimentoEstoqueRepository, never()).salvar(any());
    }

    @Test
    void deveRestaurarEstoqueAoExcluirVendaConfirmada() {
        var produto = velaComEstoque(2);
        UUID pedidoId = UUID.randomUUID();
        Pedido venda = Pedido.reconstituir(
                pedidoId,
                UUID.randomUUID(),
                LocalDate.now(),
                StatusPedido.CONFIRMADO,
                NaturezaPedido.VENDA,
                null,
                null,
                List.of(new ItemPedido(produto.id(), 2, brl("32.90"), brl("16.45"))));
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(venda));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));

        useCase.executar(pedidoId);

        verify(movimentoEstoqueRepository).salvar(any());
        verify(pedidoRepository).excluir(pedidoId);
    }
}