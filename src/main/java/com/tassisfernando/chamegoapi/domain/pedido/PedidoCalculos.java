package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.PercentualDesconto;

import java.math.BigDecimal;
import java.util.List;

public final class PedidoCalculos {

  private PedidoCalculos() {}

  public static Dinheiro totalLiquidoItem(ItemPedido item) {
    Dinheiro bruto =
        item.precoUnitario().multiplicar(BigDecimal.valueOf(item.quantidade()));
    return bruto.multiplicar(item.descontoPercentual().fatorLiquido());
  }

  public static Dinheiro subtotalItens(List<ItemPedido> itens) {
    return itens.stream()
        .map(PedidoCalculos::totalLiquidoItem)
        .reduce(Dinheiro.zero(), Dinheiro::somar);
  }

  public static Dinheiro valorTotal(List<ItemPedido> itens, PercentualDesconto descontoPedido) {
    Dinheiro subtotal = subtotalItens(itens);
    return subtotal.multiplicar(descontoPedido.fatorLiquido());
  }
}