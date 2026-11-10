package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
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
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.cliente;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private RegistrarPedidoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarPedidoUseCase(
                pedidoRepository, clienteRepository, produtoRepository, movimentoEstoqueRepository);
        lenient().when(pedidoRepository.salvar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveConfirmarPedidoBaixarEstoqueERegistrarMovimento() {
        var cliente = cliente("Hellen Vitória Lima");
        Produto produto = velaComEstoque(5);
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));

        Pedido pedido = useCase.executar(new RegistrarPedidoComando(
                cliente.id(),
                LocalDate.of(2026, 1, 24),
                NaturezaPedido.VENDA,
                null,
                List.of(new ItemPedidoComando(produto.id(), 2, null))));

        assertEquals(StatusPedido.CONFIRMADO, pedido.status());
        assertEquals(brl("65.80"), pedido.valorTotal());
        assertEquals(3, produto.estoqueAtual());
        verify(produtoRepository).salvar(produto);
        verify(movimentoEstoqueRepository).salvar(any());
        verify(pedidoRepository).salvar(pedido);
    }

    @Test
    void deveFalharQuandoClienteNaoExistir() {
        UUID clienteId = UUID.randomUUID();
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.empty());

        assertThrows(
                EntidadeNaoEncontrada.class,
                () -> useCase.executar(new RegistrarPedidoComando(
                        clienteId,
                        LocalDate.now(),
                        NaturezaPedido.VENDA,
                        null,
                        List.of(new ItemPedidoComando(UUID.randomUUID(), 1, null)))));

        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    void deveFalharQuandoEstoqueDoProdutoForInsuficiente() {
        var cliente = cliente("Eunice Santos Soares");
        Produto produto = velaComEstoque(1);
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));

        assertThrows(
                RegraDeNegocioViolada.class,
                () -> useCase.executar(new RegistrarPedidoComando(
                        cliente.id(),
                        LocalDate.now(),
                        NaturezaPedido.VENDA,
                        null,
                        List.of(new ItemPedidoComando(produto.id(), 3, null)))));
    }
}
