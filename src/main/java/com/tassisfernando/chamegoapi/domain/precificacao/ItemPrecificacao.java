package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPrecificacao(
        UUID id,
        TipoItemPrecificacao tipo,
        UUID produtoId,
        UUID insumoId,
        String descricao,
        BigDecimal quantidade,
        UnidadeMedida unidadeMedida,
        Dinheiro custoUnitario) {

    public Dinheiro subtotal() {
        return custoUnitario.multiplicar(quantidade);
    }

    public static ItemPrecificacao deInsumo(
            UUID insumoId, String descricao, BigDecimal quantidade, UnidadeMedida unidade, Dinheiro custoUnitario) {
        return new ItemPrecificacao(
                UUID.randomUUID(), TipoItemPrecificacao.INSUMO, null, insumoId, descricao, quantidade, unidade, custoUnitario);
    }

    public static ItemPrecificacao deProduto(
            UUID produtoId, String descricao, BigDecimal quantidade, Dinheiro custoUnitario) {
        return new ItemPrecificacao(
                UUID.randomUUID(),
                TipoItemPrecificacao.PRODUTO,
                produtoId,
                null,
                descricao,
                quantidade,
                UnidadeMedida.UN,
                custoUnitario);
    }

    public static ItemPrecificacao extra(String descricao, BigDecimal quantidade, Dinheiro custoUnitario) {
        return new ItemPrecificacao(
                UUID.randomUUID(),
                TipoItemPrecificacao.EXTRA,
                null,
                null,
                descricao,
                quantidade,
                UnidadeMedida.UN,
                custoUnitario);
    }

    public static ItemPrecificacao reconstituir(
            UUID id,
            TipoItemPrecificacao tipo,
            UUID produtoId,
            UUID insumoId,
            String descricao,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            Dinheiro custoUnitario) {
        return new ItemPrecificacao(
                id, tipo, produtoId, insumoId, descricao, quantidade, unidadeMedida, custoUnitario);
    }
}