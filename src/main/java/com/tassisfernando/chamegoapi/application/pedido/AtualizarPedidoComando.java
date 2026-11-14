package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AtualizarPedidoComando(
        UUID id,
        UUID clienteId,
        LocalDate dataPedido,
        NaturezaPedido natureza,
        StatusPedido status,
        String observacao,
        List<ItemPedidoComando> itens) {
}
