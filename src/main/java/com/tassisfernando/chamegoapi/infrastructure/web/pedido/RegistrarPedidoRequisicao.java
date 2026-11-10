package com.tassisfernando.chamegoapi.infrastructure.web.pedido;

import com.tassisfernando.chamegoapi.application.pedido.ItemPedidoComando;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RegistrarPedidoRequisicao(
        @NotNull UUID clienteId,
        LocalDate dataPedido,
        NaturezaPedido natureza,
        String observacao,
        @NotEmpty @Valid List<ItemRequisicao> itens) {

    public record ItemRequisicao(
            @NotNull UUID produtoId, int quantidade, BigDecimal precoUnitario) {
        public ItemPedidoComando paraComando() {
            return new ItemPedidoComando(produtoId, quantidade, precoUnitario);
        }
    }
}
