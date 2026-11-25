package com.tassisfernando.chamegoapi.domain.pedido;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendaEntregasTest {

    private final LocalDate hoje = LocalDate.of(2026, 11, 24);

    @Test
    void separaEntregaDeHojeDasProximasEIgnoraCancelado() {
        Pedido deHoje = pedido(hoje, StatusPedido.CONFIRMADO);
        Pedido amanha = pedido(hoje.plusDays(1), StatusPedido.ORCAMENTO);
        Pedido noLimite = pedido(hoje.plusDays(7), StatusPedido.CONFIRMADO);
        Pedido depoisDoLimite = pedido(hoje.plusDays(8), StatusPedido.CONFIRMADO);
        Pedido canceladoHoje = pedido(hoje, StatusPedido.CANCELADO);
        Pedido semData = pedido(null, StatusPedido.CONFIRMADO);

        assertTrue(AgendaEntregas.entregaHoje(deHoje, hoje));
        assertFalse(AgendaEntregas.entregaProxima(deHoje, hoje, 7));

        assertTrue(AgendaEntregas.entregaProxima(amanha, hoje, 7));
        assertTrue(AgendaEntregas.entregaProxima(noLimite, hoje, 7));
        assertFalse(AgendaEntregas.entregaProxima(depoisDoLimite, hoje, 7));
        assertFalse(AgendaEntregas.entregaHoje(depoisDoLimite, hoje));

        assertFalse(AgendaEntregas.entregaHoje(canceladoHoje, hoje));
        assertFalse(AgendaEntregas.entregaProxima(canceladoHoje, hoje, 7));
        assertFalse(AgendaEntregas.contaNaAgenda(semData));
    }

    private static Pedido pedido(LocalDate dataEntrega, StatusPedido status) {
        return Pedido.reconstituir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(2026, 11, 1),
                status,
                NaturezaPedido.VENDA,
                null,
                null,
                dataEntrega,
                List.of(new ItemPedido(UUID.randomUUID(), 1, brl("10.00"), brl("4.00"))));
    }
}
