package com.tassisfernando.chamegoapi.application.pedido;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoComando(UUID produtoId, int quantidade, BigDecimal precoUnitario) {
}
