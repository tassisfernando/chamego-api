package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedido(
        UUID produtoId,
        int quantidade,
        Dinheiro precoUnitario,
        Dinheiro custoUnitario) {

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
    }

    public Dinheiro receitaBruta() {
        return precoUnitario.multiplicar(BigDecimal.valueOf(quantidade));
    }

    public Dinheiro custoTotal() {
        return custoUnitario.multiplicar(BigDecimal.valueOf(quantidade));
    }

    public Dinheiro lucroItem() {
        return receitaBruta().subtrair(custoTotal());
    }
}
