package com.tassisfernando.chamegoapi.domain.pedido;

import java.time.LocalDate;

/** Recorte de pedidos com entrega agendada, excluindo cancelados. */
public final class AgendaEntregas {

    private AgendaEntregas() {}

    public static boolean contaNaAgenda(Pedido pedido) {
        return pedido != null
                && pedido.status() != StatusPedido.CANCELADO
                && pedido.dataEntrega() != null;
    }

    public static boolean entregaHoje(Pedido pedido, LocalDate hoje) {
        return contaNaAgenda(pedido) && pedido.dataEntrega().equals(hoje);
    }

    /** Entregas estritamente depois de {@code hoje} e até {@code hoje + dias} (inclusive). */
    public static boolean entregaProxima(Pedido pedido, LocalDate hoje, int dias) {
        if (!contaNaAgenda(pedido) || dias < 1) {
            return false;
        }
        LocalDate data = pedido.dataEntrega();
        LocalDate limite = hoje.plusDays(dias);
        return data.isAfter(hoje) && !data.isAfter(limite);
    }
}
