package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RegistrarPedidoComando(
        UUID clienteId,
        LocalDate dataPedido,
        NaturezaPedido natureza,
        String observacao,
        BigDecimal descontoPercentual,
        boolean confirmarVenda,
        List<ItemPedidoComando> itens) {
}