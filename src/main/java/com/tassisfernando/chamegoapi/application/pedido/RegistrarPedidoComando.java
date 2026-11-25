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
        List<ItemPedidoComando> itens,
        LocalDate dataEntrega) {

    public RegistrarPedidoComando(
            UUID clienteId,
            LocalDate dataPedido,
            NaturezaPedido natureza,
            String observacao,
            BigDecimal descontoPercentual,
            boolean confirmarVenda,
            List<ItemPedidoComando> itens) {
        this(
                clienteId,
                dataPedido,
                natureza,
                observacao,
                descontoPercentual,
                confirmarVenda,
                itens,
                null);
    }
}