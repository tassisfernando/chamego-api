package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.PercentualDesconto;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedido(
        UUID produtoId,
        int quantidade,
        Dinheiro precoUnitario,
        Dinheiro custoUnitario,
        PercentualDesconto descontoPercentual) {

    public ItemPedido(UUID produtoId, int quantidade, Dinheiro precoUnitario, Dinheiro custoUnitario) {
        this(produtoId, quantidade, precoUnitario, custoUnitario, PercentualDesconto.zero());
    }

    public ItemPedido {
        if (produtoId == null) {
            throw new RegraDeNegocioViolada("produto do item é obrigatório");
        }
        if (quantidade <= 0) {
            throw new RegraDeNegocioViolada("quantidade do item deve ser positiva");
        }
        if (precoUnitario == null) {
            precoUnitario = Dinheiro.zero();
        }
        if (custoUnitario == null) {
            custoUnitario = Dinheiro.zero();
        }
        if (descontoPercentual == null) {
            descontoPercentual = PercentualDesconto.zero();
        }
    }

    public Dinheiro receitaBruta() {
        return PedidoCalculos.totalLiquidoItem(this);
    }

    public Dinheiro receitaSemDescontoItem() {
        return precoUnitario.multiplicar(BigDecimal.valueOf(quantidade));
    }

    public Dinheiro custoTotal() {
        return custoUnitario.multiplicar(BigDecimal.valueOf(quantidade));
    }

    public Dinheiro lucroItem() {
        return receitaBruta().subtrair(custoTotal());
    }
}