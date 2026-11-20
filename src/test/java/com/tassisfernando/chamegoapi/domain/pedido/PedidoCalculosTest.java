package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.PercentualDesconto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoCalculosTest {

    private static Dinheiro brl(String v) {
        return Dinheiro.de(new BigDecimal(v));
    }

    @Test
    void deveAplicarDescontoNoItem() {
        var item = new ItemPedido(
                UUID.randomUUID(), 2, brl("50.00"), brl("20.00"), PercentualDesconto.de(new BigDecimal("10")));
        assertEquals(new BigDecimal("90.00"), PedidoCalculos.totalLiquidoItem(item).valor());
    }

    @Test
    void deveCombinarDescontosDeItemEPedido() {
        var item = new ItemPedido(
                UUID.randomUUID(), 1, brl("100.00"), brl("40.00"), PercentualDesconto.de(new BigDecimal("10")));
        var total = PedidoCalculos.valorTotal(List.of(item), PercentualDesconto.de(new BigDecimal("5")));
        assertEquals(new BigDecimal("85.50"), total.valor());
    }
}