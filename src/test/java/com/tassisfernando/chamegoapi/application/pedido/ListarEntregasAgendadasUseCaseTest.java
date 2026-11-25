package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarEntregasAgendadasUseCaseTest {

    private final LocalDate hoje = LocalDate.of(2026, 11, 24);

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    private ListarEntregasAgendadasUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarEntregasAgendadasUseCase(pedidoRepository, clienteRepository, produtoRepository);
    }

    @Test
    void separaHojeDeProximasEDescartaCanceladoEForaDoHorizonte() {
        Cliente cliente = Cliente.novo("Lia", "31999990000", "Ipatinga", "MG", LocalDate.of(1990, 1, 2));
        Pedido deHoje = pedido(cliente.id(), hoje, StatusPedido.CONFIRMADO);
        Pedido amanha = pedido(cliente.id(), hoje.plusDays(2), StatusPedido.ORCAMENTO);
        Pedido longe = pedido(cliente.id(), hoje.plusDays(8), StatusPedido.CONFIRMADO);
        Pedido cancelado = pedido(cliente.id(), hoje, StatusPedido.CANCELADO);
        when(pedidoRepository.listarPorDataEntrega(hoje, hoje.plusDays(7)))
                .thenReturn(List.of(deHoje, amanha, longe, cancelado));
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(produtoRepository.buscarPorId(org.mockito.ArgumentMatchers.any())).thenReturn(Optional.empty());

        PainelEntregas painel = useCase.executar(hoje, 7);

        assertEquals(1, painel.hoje().size());
        assertEquals(deHoje.id(), painel.hoje().getFirst().id());
        assertTrue(painel.hoje().getFirst().hoje());
        assertEquals(0, painel.hoje().getFirst().diasAteEntrega());
        assertEquals(1, painel.proximas().size());
        assertEquals(amanha.id(), painel.proximas().getFirst().id());
        assertEquals(2, painel.proximas().getFirst().diasAteEntrega());
    }

    private static Pedido pedido(UUID clienteId, LocalDate dataEntrega, StatusPedido status) {
        return Pedido.reconstituir(
                UUID.randomUUID(),
                clienteId,
                LocalDate.of(2026, 11, 1),
                status,
                NaturezaPedido.VENDA,
                null,
                null,
                dataEntrega,
                List.of(new ItemPedido(UUID.randomUUID(), 1, brl("20.00"), brl("8.00"))));
    }
}
