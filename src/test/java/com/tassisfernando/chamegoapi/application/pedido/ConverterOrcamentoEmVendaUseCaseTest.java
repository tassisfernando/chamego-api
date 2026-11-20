package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConverterOrcamentoEmVendaUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private ConverterOrcamentoEmVendaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConverterOrcamentoEmVendaUseCase(
                pedidoRepository, new PedidoEstoqueService(produtoRepository, movimentoEstoqueRepository));
    }

    @Test
    void deveConcretizarOrcamento() {
        var produto = velaComEstoque(4);
        UUID pedidoId = UUID.randomUUID();
        Pedido orcamento = Pedido.reconstituir(
                pedidoId,
                UUID.randomUUID(),
                LocalDate.now(),
                StatusPedido.ORCAMENTO,
                NaturezaPedido.VENDA,
                null,
                null,
                List.of(new ItemPedido(produto.id(), 2, brl("32.90"), brl("16.45"))));
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(orcamento));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(pedidoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pedido result = useCase.executar(pedidoId);

        assertEquals(StatusPedido.CONFIRMADO, result.status());
        assertEquals(2, produto.estoqueAtual());
        verify(movimentoEstoqueRepository).salvar(any());
    }

    @Test
    void deveFalharSeNaoForOrcamento() {
        UUID pedidoId = UUID.randomUUID();
        Pedido confirmado = Pedido.reconstituir(
                pedidoId,
                UUID.randomUUID(),
                LocalDate.now(),
                StatusPedido.CONFIRMADO,
                NaturezaPedido.VENDA,
                null,
                null,
                List.of(new ItemPedido(UUID.randomUUID(), 1, brl("10"), brl("5"))));
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(confirmado));

        assertThrows(RegraDeNegocioViolada.class, () -> useCase.executar(pedidoId));
    }
}